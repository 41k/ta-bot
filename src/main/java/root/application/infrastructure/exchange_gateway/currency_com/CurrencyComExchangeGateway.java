package root.application.infrastructure.exchange_gateway.currency_com;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import root.application.configuration.ExchangeGatewayConfiguration;
import root.application.domain.history.BarRepository;
import root.application.domain.trading.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toConcurrentMap;
import static java.util.stream.Collectors.toSet;
import static root.application.domain.trading.ExchangeGatewayAccountConfigurationProperty.API_KEY;
import static root.application.domain.trading.ExchangeGatewayAccountConfigurationProperty.SECRET_KEY;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Slf4j
public class CurrencyComExchangeGateway implements ExchangeGateway
{
    private static final String BASE_API_URL = "api-adapter.backend.currency.com";
    private static final URI WSS_CONNECTION_URI = URI.create("wss://" + BASE_API_URL + "/connect");
    private static final String BASE_REST_API_URL = "https://" + BASE_API_URL + "/api/v1";
    //private static final String BASE_REST_API_URL = "http://localhost:8888/api/v1";
    private static final String ORDER_REST_API_URL = "/order";

    private static final long TIMEOUT_BETWEEN_WS_SESSIONS_IN_SECONDS = 10L;
    private static final long PING_INTERVAL_IN_SECONDS = 20L;
    private static final long PING_INITIAL_DELAY = 20L;
    private static final String PING_REQUEST = "{\"destination\":\"ping\"}";
    private static final String OHLC_WSS_ENDPOINT = "OHLCMarketData.subscribe";

    private static final String SYMBOL_KEY = "symbol";
    private static final String INTERVAL_KEY = "interval";
    private static final String TIMESTAMP_KEY = "t";
    private static final String OPEN_PRICE_KEY = "o";
    private static final String HIGH_PRICE_KEY = "h";
    private static final String LOW_PRICE_KEY = "l";
    private static final String CLOSE_PRICE_KEY = "c";
    private static final String VOLUME = "1";

    private static final String API_KEY_HEADER_NAME = "X-MBX-APIKEY";
    private static final String SYMBOL_PARAM = "symbol";
    private static final String QUANTITY_PARAM = "quantity";
    private static final String TIME_IN_FORCE_PARAM = "timeInForce";
    private static final String TIME_IN_FORCE_VALUE = "FOK";
    private static final String TIMESTAMP_PARAM = "timestamp";
    private static final String RECV_WINDOW_PARAM = "recvWindow";
    private static final String RECV_WINDOW_VALUE = "5000";
    private static final String ORDER_TYPE_PARAM = "type";
    private static final String ORDER_TYPE_VALUE = "MARKET";
    private static final String ORDER_SIDE_PARAM = "side";
    private static final String BUY_ORDER_SIDE = "BUY";
    private static final String SELL_ORDER_SIDE = "SELL";
    private static final String SIGNATURE_PARAM = "signature";
    private static final String ORDER_SUCCESS_STATUS = "FILLED";

    private static final String REQUEST_PARAM_VALUE_FORMAT = "%s=%s";
    private static final String REQUEST_PARAMS_DELIMITER = "&";
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final String SIGNATURE_HASHING_ALGORITHM = "HmacSHA256";

    private final ExchangeGatewayConfiguration configuration;
    private final Map<BarStreamSubscription, Map<String, Consumer<Bar>>> barStreamSubscribersStore;
    private final WebSocketClient webSocketClient;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final BarRepository barRepository;
    private ScheduledExecutorService pingRequestRecurrentExecutor;

    public CurrencyComExchangeGateway(ExchangeGatewayConfiguration configuration,
                                      WebSocketClient webSocketClient,
                                      ObjectMapper objectMapper,
                                      BarRepository barRepository)
    {
        this.configuration = configuration;
        this.barStreamSubscribersStore = buildBarStreamSubscribersStore();
        this.webSocketClient = webSocketClient;
        this.webClient = buildWebClient();
        this.objectMapper = objectMapper;
        this.barRepository = barRepository;
        run();
    }

    @Override
    public String getId()
    {
        return configuration.getId();
    }

    @Override
    public String getName()
    {
        return configuration.getName();
    }

    @Override
    public double getTransactionFee()
    {
        return configuration.getTransactionFee();
    }

    @Override
    public Collection<Symbol> getSupportedSymbols()
    {
        return configuration.getSymbols();
    }

    @Override
    public Collection<Interval> getSupportedIntervals()
    {
        return configuration.getIntervals();
    }

    @Override
    public Collection<ExchangeGatewayAccountConfigurationProperty> getSupportedAccountConfigurationProperties()
    {
        return Set.of(API_KEY, SECRET_KEY);
    }

    @Override
    public void subscribeToBarStream(BarStreamSubscriptionContext subscriptionContext)
    {
        var subscribers = getSubscribers(subscriptionContext);
        var subscriberId = subscriptionContext.getBarStreamSubscriberId();
        var subscriber = subscriptionContext.getBarStreamSubscriber();
        subscribers.put(subscriberId, subscriber);
    }

    @Override
    public void unsubscribeFromBarStream(BarStreamSubscriptionContext subscriptionContext)
    {
        var subscribers = getSubscribers(subscriptionContext);
        var subscriberId = subscriptionContext.getBarStreamSubscriberId();
        subscribers.remove(subscriberId);
    }

    @Override
    public TradingOperationResult buy(TradingOperationContext context)
    {
        var orderResult = makeOrder(BUY_ORDER_SIDE, context);
        return buildTradingOperationResult(orderResult, context);
    }

    @Override
    public TradingOperationResult sell(TradingOperationContext context)
    {
        var orderResult = makeOrder(SELL_ORDER_SIDE, context);
        return buildTradingOperationResult(orderResult, context);
    }

    private Map<BarStreamSubscription, Map<String, Consumer<Bar>>> buildBarStreamSubscribersStore()
    {
        return configuration.getIntervals().stream()
            .map(interval ->
                configuration.getSymbols().stream()
                    .map(symbol -> new BarStreamSubscription(symbol, interval))
                    .collect(toSet()))
            .flatMap(Collection::stream)
            .collect(toConcurrentMap(
                Function.identity(),
                subscription -> new ConcurrentHashMap<>()
            ));
    }

    private WebClient buildWebClient()
    {
        return WebClient.builder()
            .baseUrl(BASE_REST_API_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .build();
    }

    private void run()
    {
        log.info("Exchange gateway [{}] is starting...", configuration.getName());
        establishWebSocketSession();
    }

    private void establishWebSocketSession()
    {
        webSocketClient.execute(WSS_CONNECTION_URI, this::handleWebSocketSession)
            .doOnError(throwable -> log.error(throwable.getMessage(), throwable))
            .doOnTerminate(this::handleWebSocketSessionTermination)
            .subscribe();
    }

    private Mono<Void> handleWebSocketSession(WebSocketSession webSocketSession)
    {
        return pingWebSocketServerRecurrently(webSocketSession)
            .then(sendOhlcSubscriptionRequest(webSocketSession))
            .then(processIncomingMessages(webSocketSession));
    }

    @SneakyThrows
    private void handleWebSocketSessionTermination()
    {
        log.warn("WS session has been terminated. Stop pinging WS server.");
        ofNullable(pingRequestRecurrentExecutor).ifPresent(ScheduledExecutorService::shutdownNow);
        log.info("Exchange gateway [{}] will be re-run after {} seconds.",
            configuration.getName(), TIMEOUT_BETWEEN_WS_SESSIONS_IN_SECONDS);
        TimeUnit.SECONDS.sleep(TIMEOUT_BETWEEN_WS_SESSIONS_IN_SECONDS);
        run();
    }

    private Mono<Void> pingWebSocketServerRecurrently(WebSocketSession webSocketSession)
    {
        pingRequestRecurrentExecutor = Executors.newScheduledThreadPool(1);
        pingRequestRecurrentExecutor.scheduleAtFixedRate(
            () -> pingWebSocketServer(webSocketSession),
            PING_INITIAL_DELAY,
            PING_INTERVAL_IN_SECONDS,
            TimeUnit.SECONDS);
        return Mono.empty();
    }

    private void pingWebSocketServer(WebSocketSession webSocketSession)
    {
        var pingRequest = Mono.just(webSocketSession.textMessage(PING_REQUEST));
        webSocketSession.send(pingRequest).subscribe();
    }

    @SneakyThrows
    private Mono<Void> sendOhlcSubscriptionRequest(WebSocketSession webSocketSession)
    {
        var symbols = configuration.getSymbolsRepresentations();
        var intervals = configuration.getIntervalsRepresentations();
        var request = new OhlcSubscriptionRequest(OHLC_WSS_ENDPOINT, symbols, intervals);
        var requestJson = objectMapper.writeValueAsString(request);
        log.info("OHLC WSS request: {}", requestJson);
        return webSocketSession.send(Mono.just(webSocketSession.textMessage(requestJson)));
    }

    private Mono<Void> processIncomingMessages(WebSocketSession webSocketSession)
    {
        return webSocketSession.receive()
            .doOnNext(WebSocketMessage::retain)
            .publishOn(Schedulers.boundedElastic())
            .map(this::processIncomingMessage)
            .doOnNext(WebSocketMessage::release)
            .then();
    }

    @SneakyThrows
    private WebSocketMessage processIncomingMessage(WebSocketMessage message)
    {
        var messagePayloadString = message.getPayloadAsText();
        var wssIncomingMessage = objectMapper.readValue(messagePayloadString, WssIncomingMessage.class);
        if (!wssIncomingMessage.hasOkStatus())
        {
            log.warn(messagePayloadString);
            return message;
        }
        log.info(messagePayloadString);
        if (wssIncomingMessage.isOhlcMessage())
        {
            processOhlcMessage(wssIncomingMessage);
        }
        return message;
    }

    private void processOhlcMessage(WssIncomingMessage ohlcMessage)
    {
        var payload = ohlcMessage.getPayload();
        var symbol = extractSymbol(payload);
        var interval = extractInterval(payload);
        var bar = buildBar(interval, payload);
        var exchangeGatewayName = configuration.getName();
        var subscription = new BarStreamSubscription(symbol, interval);
        var subscribers = barStreamSubscribersStore.get(subscription);
        if (nonNull(subscribers))
        {
            barRepository.save(bar, exchangeGatewayName, symbol, interval);
            subscribers.values().forEach(subscriber -> subscriber.accept(bar));
        }
        else
        {
            log.warn("Bar has been skipped because {} is not supported by exchange gateway [{}].", subscription, configuration.getName());
        }
    }

    private Symbol extractSymbol(Map<String, Object> payload)
    {
        var symbolRepresentation = String.valueOf(payload.get(SYMBOL_KEY));
        return configuration.getSymbol(symbolRepresentation);
    }

    private Interval extractInterval(Map<String, Object> payload)
    {
        var intervalRepresentation = String.valueOf(payload.get(INTERVAL_KEY));
        return configuration.getInterval(intervalRepresentation);
    }

    private Bar buildBar(Interval interval, Map<String, Object> ohlc)
    {
        var duration = interval.getDuration();
        var barTime = Instant.ofEpochMilli(Long.parseLong(String.valueOf(ohlc.get(TIMESTAMP_KEY))));
        var zonedBarTime = ZonedDateTime.ofInstant(barTime, ZoneId.systemDefault());
        var open = String.valueOf(ohlc.get(OPEN_PRICE_KEY));
        var high = String.valueOf(ohlc.get(HIGH_PRICE_KEY));
        var low = String.valueOf(ohlc.get(LOW_PRICE_KEY));
        var close = String.valueOf(ohlc.get(CLOSE_PRICE_KEY));
        return new BaseBar(duration, zonedBarTime, open, high, low, close, VOLUME);
    }

    private Map<String, Consumer<Bar>> getSubscribers(BarStreamSubscriptionContext subscriptionContext)
    {
        var symbol = subscriptionContext.getSymbol();
        var interval = subscriptionContext.getInterval();
        var subscription = new BarStreamSubscription(symbol, interval);
        return ofNullable(barStreamSubscribersStore.get(subscription))
            .orElseThrow(() -> new IllegalArgumentException(format(
                "Exchange gateway [%s] does not support %s.", configuration.getName(), subscription.toString())));
    }

    @SneakyThrows
    private OrderResult makeOrder(String orderSide, TradingOperationContext context)
    {
        var apiKey = ofNullable(context.getExchangeGatewayAccountConfiguration().get(API_KEY))
            .orElseThrow(() -> new NoSuchElementException("API key is not provided."));
        var requestBody = buildOrderRequestBody(orderSide, context);
        var responseBody = webClient.post()
            .uri(ORDER_REST_API_URL)
            .header(API_KEY_HEADER_NAME, apiKey)
            .accept(MediaType.APPLICATION_JSON)
            .acceptCharset(UTF_8)
            .bodyValue(requestBody)
            .exchange()
            .flatMap(clientResponse -> {
                if (clientResponse.statusCode().isError())
                {
                    log.error("Order execution response status is [{}]", clientResponse.rawStatusCode());
                }
                return clientResponse.bodyToMono(String.class);
            })
            .block();
        log.info("Order execution response body: {}.", responseBody);
        return objectMapper.readValue(responseBody, OrderResult.class);
    }

    private String buildOrderRequestBody(String orderSide, TradingOperationContext context)
    {
        var symbol = context.getSymbol();
        var symbolRepresentation = configuration.getSymbolRepresentation(symbol);
        var amount = context.getAmount().toString();
        var requestBody = Map.of(
            TIME_IN_FORCE_PARAM, TIME_IN_FORCE_VALUE,
            RECV_WINDOW_PARAM, RECV_WINDOW_VALUE,
            ORDER_TYPE_PARAM, ORDER_TYPE_VALUE,
            SYMBOL_PARAM, symbolRepresentation,
            QUANTITY_PARAM, amount,
            TIMESTAMP_PARAM, String.valueOf(System.currentTimeMillis()),
            ORDER_SIDE_PARAM, orderSide
        ).entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(this::buildRequestParamValuePair)
            .collect(Collectors.joining(REQUEST_PARAMS_DELIMITER));
        var secretKey = ofNullable(context.getExchangeGatewayAccountConfiguration().get(SECRET_KEY))
            .orElseThrow(() -> new NoSuchElementException("Secret key is not provided."));
        return signRequestBody(requestBody, secretKey);
    }

    private String buildRequestParamValuePair(Map.Entry<String, String> entry)
    {
        return buildRequestParamValuePair(entry.getKey(), entry.getValue());
    }

    @SneakyThrows
    private String buildRequestParamValuePair(String param, String value)
    {
        var encodedValue = URLEncoder.encode(value, UTF_8.toString());
        return format(REQUEST_PARAM_VALUE_FORMAT, param, encodedValue);
    }

    @SneakyThrows
    private String signRequestBody(String requestBody, String key)
    {
        var sha256HMAC = Mac.getInstance(SIGNATURE_HASHING_ALGORITHM);
        var secretKey = new SecretKeySpec(key.getBytes(UTF_8), SIGNATURE_HASHING_ALGORITHM);
        sha256HMAC.init(secretKey);
        var signatureValue = Hex.encodeHexString(sha256HMAC.doFinal(requestBody.getBytes(UTF_8)));
        var signatureParamValuePair = buildRequestParamValuePair(SIGNATURE_PARAM, signatureValue);
        return requestBody + REQUEST_PARAMS_DELIMITER + signatureParamValuePair;
    }

    private TradingOperationResult buildTradingOperationResult(OrderResult orderResult, TradingOperationContext context)
    {
        validate(orderResult);
        var executedAmount = getExecutedAmount(orderResult, context);
        var executedPrice = getExecutedPrice(orderResult, context);
        return new TradingOperationResult(executedAmount, executedPrice);
    }

    private void validate(OrderResult orderResult)
    {
        var orderStatus = orderResult.getStatus();
        if (!ORDER_SUCCESS_STATUS.equals(orderStatus))
        {
            throw new RuntimeException(format("Order has been considered as FAILED due to status [%s].", orderStatus));
        }
        var fills = orderResult.getFills();
        if (isEmpty(fills))
        {
            throw new RuntimeException("Order result does not contain fills.");
        }
        if (fills.size() > 1)
        {
            log.warn("Order result contains more than 1 fill.");
        }
    }

    private Double getExecutedAmount(OrderResult orderResult, TradingOperationContext context)
    {
        var originalAmount = context.getAmount();
        var executedAmount = Double.valueOf(orderResult.getExecutedQty());
        if (!originalAmount.equals(executedAmount))
        {
            log.warn("Original amount [{}] is different from executed amount [{}].", originalAmount, executedAmount);
        }
        return executedAmount;
    }

    private Double getExecutedPrice(OrderResult orderResult, TradingOperationContext context)
    {
        var originalPrice = context.getPrice();
        var fill = orderResult.getFills().get(0);
        var executedPrice = Double.valueOf(fill.getPrice());
        if (!originalPrice.equals(executedPrice))
        {
            log.warn("Original price [{}] is different from executed price [{}].", originalPrice, executedPrice);
        }
        return executedPrice;
    }
}
