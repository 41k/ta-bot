package root.application.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.application.application.model.HistoryFilter;
import root.application.application.model.StrategyExecutionInfo;
import root.application.application.service.HistoryService;
import root.application.domain.history.TradeHistoryItem;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryApiController
{
    private final HistoryService historyService;

    @GetMapping("/exchange-gateways")
    public List<String> getExchangeGateways(HistoryFilter filter)
    {
        return historyService.searchForExchangeGateways(filter);
    }

    @GetMapping("/strategy-executions")
    public List<StrategyExecutionInfo> getStrategyExecutions(HistoryFilter filter)
    {
        return historyService.searchForStrategyExecutions(filter);
    }

    @GetMapping("/trades")
    public List<TradeHistoryItem> getTradesHistory(HistoryFilter filter, Pageable pageable)
    {
        return historyService.searchForTrades(filter, pageable);
    }
}
