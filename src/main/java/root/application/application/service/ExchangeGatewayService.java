package root.application.application.service;

import lombok.RequiredArgsConstructor;
import root.application.application.model.ActivatedExchangeGateway;
import root.application.application.model.ExchangeGatewayAccount;
import root.application.application.model.NotActivatedExchangeGateway;
import root.application.domain.trading.ExchangeGateway;
import root.application.domain.trading.ExchangeGatewayAccountConfigurationProperty;
import root.application.domain.trading.Interval;
import root.application.domain.trading.Symbol;

import java.util.*;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static root.application.domain.trading.ExchangeGatewayAccountConfigurationProperty.API_KEY;
import static root.application.domain.trading.ExchangeGatewayAccountConfigurationProperty.SECRET_KEY;

@RequiredArgsConstructor
public class ExchangeGatewayService
{
    private static final Set<ExchangeGatewayAccountConfigurationProperty> CONFIGURATION_PROPERTIES_TO_MASK = Set.of(API_KEY, SECRET_KEY);
    private static final int MASKED_VALUE_VISIBLE_PART_LENGTH = 4;
    private static final String MASKED_VALUE_FORMAT = "%s*****";

    private final Map<String, ExchangeGateway> exchangeGatewaysStore;
    private final ExchangeGatewayAccountService exchangeGatewayAccountService;

    public ExchangeGateway getExchangeGateway(String exchangeGatewayId)
    {
        return ofNullable(exchangeGatewaysStore.get(exchangeGatewayId)).orElseThrow(
            () -> new NoSuchElementException(format("Exchange gateway with id [%s] is not found.", exchangeGatewayId)));
    }

    public Collection<ActivatedExchangeGateway> getActivatedExchangeGateways()
    {
        return exchangeGatewayAccountService.getAllAccounts().stream()
            .map(this::buildActivatedExchangeGateway)
            .collect(toList());
    }

    public Collection<NotActivatedExchangeGateway> getNotActivatedExchangeGateways()
    {
        var idsOfActivatedExchangeGateways = exchangeGatewayAccountService.getAllAccounts().stream()
            .map(ExchangeGatewayAccount::getExchangeGatewayId)
            .collect(toList());
        return exchangeGatewaysStore.values().stream()
            .filter(exchangeGateway -> !idsOfActivatedExchangeGateways.contains(exchangeGateway.getId()))
            .map(exchangeGateway -> new NotActivatedExchangeGateway(exchangeGateway.getId(), exchangeGateway.getName()))
            .collect(toList());
    }

    public Collection<Interval> getSupportedIntervals(String exchangeGatewayId)
    {
        return getExchangeGateway(exchangeGatewayId).getSupportedIntervals();
    }

    public Collection<Symbol> getSupportedSymbols(String exchangeGatewayId)
    {
        return getExchangeGateway(exchangeGatewayId).getSupportedSymbols();
    }

    public Collection<ExchangeGatewayAccountConfigurationProperty> getSupportedAccountConfigurationProperties(String exchangeGatewayId)
    {
        return getExchangeGateway(exchangeGatewayId).getSupportedAccountConfigurationProperties();
    }

    private ActivatedExchangeGateway buildActivatedExchangeGateway(ExchangeGatewayAccount account)
    {
        var exchangeGatewayId = account.getExchangeGatewayId();
        var exchangeGateway = getExchangeGateway(exchangeGatewayId);
        return ActivatedExchangeGateway.builder()
            .id(exchangeGateway.getId())
            .name(exchangeGateway.getName())
            .accountId(account.getId())
            .accountConfiguration(buildMaskedConfiguration(account))
            .build();
    }

    private Map<ExchangeGatewayAccountConfigurationProperty, String> buildMaskedConfiguration(ExchangeGatewayAccount account)
    {
        var maskedConfiguration = new HashMap<ExchangeGatewayAccountConfigurationProperty, String>();
        account.getConfiguration().forEach((propertyName, propertyValue) ->
            maskedConfiguration.put(propertyName, formMaskedPropertyValue(propertyName, propertyValue)));
        return maskedConfiguration;
    }

    private String formMaskedPropertyValue(ExchangeGatewayAccountConfigurationProperty propertyName, String propertyValue)
    {
        if (!CONFIGURATION_PROPERTIES_TO_MASK.contains(propertyName))
        {
            return propertyValue;
        }
        var visiblePartOfValue = propertyValue.substring(0, MASKED_VALUE_VISIBLE_PART_LENGTH);
        return format(MASKED_VALUE_FORMAT, visiblePartOfValue);
    }
}
