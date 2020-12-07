package root.application.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.application.application.model.ExchangeGatewayInfo;
import root.application.application.service.ExchangeGatewayService;
import root.application.domain.trading.Interval;

import java.util.Set;

@RestController
@RequestMapping("/api/exchange-gateways")
@RequiredArgsConstructor
public class ExchangeGatewayApiController
{
    private final ExchangeGatewayService exchangeGatewayService;

    @GetMapping
    public Set<ExchangeGatewayInfo> getExchangeGateways()
    {
        return exchangeGatewayService.getExchangeGateways();
    }

    @GetMapping("/{exchangeGatewayId}/supported-intervals")
    public Set<Interval> getSupportedIntervals(@PathVariable String exchangeGatewayId)
    {
        return exchangeGatewayService.getSupportedIntervals(exchangeGatewayId);
    }

    @GetMapping("/{exchangeGatewayId}/supported-symbols")
    public Set<String> getSupportedSymbols(@PathVariable String exchangeGatewayId)
    {
        return exchangeGatewayService.getSupportedSymbols(exchangeGatewayId);
    }
}
