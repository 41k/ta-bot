package root.application.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.application.application.model.ActivatedExchangeGateway;
import root.application.application.model.NotActivatedExchangeGateway;
import root.application.application.service.ExchangeGatewayService;
import root.application.domain.trading.ExchangeGatewayAccountConfigurationProperty;
import root.application.domain.trading.Interval;
import root.application.domain.trading.Symbol;

import java.util.Collection;

@RestController
@RequestMapping("/api/exchange-gateways")
@RequiredArgsConstructor
public class ExchangeGatewayApiController
{
    private final ExchangeGatewayService exchangeGatewayService;

    @GetMapping("/activated")
    public Collection<ActivatedExchangeGateway> getActivatedExchangeGateways()
    {
        return exchangeGatewayService.getActivatedExchangeGateways();
    }

    @GetMapping("/not-activated")
    public Collection<NotActivatedExchangeGateway> getNotActivatedExchangeGateways()
    {
        return exchangeGatewayService.getNotActivatedExchangeGateways();
    }

    @GetMapping("/{exchangeGatewayId}/supported-symbols")
    public Collection<Symbol> getSupportedSymbols(@PathVariable String exchangeGatewayId)
    {
        return exchangeGatewayService.getSupportedSymbols(exchangeGatewayId);
    }

    @GetMapping("/{exchangeGatewayId}/supported-intervals")
    public Collection<Interval> getSupportedIntervals(@PathVariable String exchangeGatewayId)
    {
        return exchangeGatewayService.getSupportedIntervals(exchangeGatewayId);
    }

    @GetMapping("/{exchangeGatewayId}/supported-account-configuration-properties")
    public Collection<ExchangeGatewayAccountConfigurationProperty> getSupportedAccountConfigurationProperties(@PathVariable String exchangeGatewayId)
    {
        return exchangeGatewayService.getSupportedAccountConfigurationProperties(exchangeGatewayId);
    }
}
