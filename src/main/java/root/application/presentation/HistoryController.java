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
import root.application.domain.report.TradeHistoryItem;
import root.application.infrastructure.persistence.HistoryDataProvider;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController
{
    private final HistoryDataProvider historyDataProvider;

    @GetMapping(value = "/trades", params = {"fromTimestamp", "toTimestamp"})
    public Mono<ResponseEntity<List<TradeHistoryItem>>> getTradesHistory(@RequestParam(value = "fromTimestamp") Long fromTimestamp,
                                                                         @RequestParam(value = "toTimestamp") Long toTimestamp,
                                                                         ServerHttpRequest request,
                                                                         Pageable pageable)
    {
        var trades = historyDataProvider.findAllTradesInRange(fromTimestamp, toTimestamp).collectList().block();
        return Mono.just(1000)
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(trades));
    }
}
