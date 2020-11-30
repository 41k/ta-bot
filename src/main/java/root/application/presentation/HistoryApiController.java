package root.application.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.application.application.HistoryFilter;
import root.application.application.HistoryService;
import root.application.domain.report.TradeHistoryItem;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryApiController
{
    private final HistoryService historyService;

    @GetMapping("/exchange-gateways")
    public List<String> getExchanges(HistoryFilter filter)
    {
        return historyService.searchForExchanges(filter);
    }

    @GetMapping("/strategies")
    public List<String> getStrategies(HistoryFilter filter)
    {
        return historyService.searchForStrategies(filter);
    }

    @GetMapping("/trades")
    public List<TradeHistoryItem> getTradesHistory(HistoryFilter filter, Pageable pageable)
    {
        return historyService.searchForTrades(filter, pageable);
    }
}
