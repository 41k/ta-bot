package root.application.presentation;

import io.github.jhipster.web.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import root.application.application.HistoryService;
import root.application.domain.report.TradeHistoryItem;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController
{
    private final HistoryService historyService;

    @GetMapping("/trades")
    public Mono<ResponseEntity<List<TradeHistoryItem>>> getTradesHistory(@RequestParam Long fromTimestamp,
                                                                         @RequestParam Long toTimestamp,
                                                                         @RequestParam(required = false) String exchangeId,
                                                                         @RequestParam(required = false) String strategyId,
                                                                         ServerHttpRequest request,
                                                                         Pageable pageable)
    {
        var trades = historyService.searchForTrades(fromTimestamp, toTimestamp, exchangeId, strategyId);
        return Mono.just(1000)
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(trades));
    }

    @GetMapping("/exchanges")
    public List<String> getExchanges(@RequestParam Long fromTimestamp,
                                     @RequestParam Long toTimestamp)
    {
        return historyService.searchForExchanges(fromTimestamp, toTimestamp);
    }

    @GetMapping("/strategies")
    public List<String> getStrategies(@RequestParam Long fromTimestamp,
                                      @RequestParam Long toTimestamp,
                                      @RequestParam String exchangeId)
    {
        return historyService.searchForStrategies(fromTimestamp, toTimestamp, exchangeId);
    }
}
