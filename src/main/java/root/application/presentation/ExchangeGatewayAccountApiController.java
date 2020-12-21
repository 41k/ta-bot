package root.application.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import root.application.application.model.command.CreateExchangeGatewayAccountCommand;
import root.application.application.model.command.DeleteExchangeGatewayAccountCommand;
import root.application.application.model.command.RunStrategyExecutionCommand;
import root.application.application.service.ExchangeGatewayAccountService;
import root.application.domain.trading.ExchangeGatewayAccountConfigurationProperty;

import java.util.Map;

@RestController
@RequestMapping("/api/exchange-gateways/{exchangeGatewayId}/accounts")
@RequiredArgsConstructor
public class ExchangeGatewayAccountApiController
{
    private final ExchangeGatewayAccountService exchangeGatewayAccountService;

    @PostMapping
    public void createAccount(@PathVariable String exchangeGatewayId,
                              @RequestBody Map<ExchangeGatewayAccountConfigurationProperty, String> configuration)
    {
        // Todo: Fix it
        // By default calls to this endpoint is executed on reactor-http-nio-* thread which does not allow blocking operations.
        // So it is necessary to switch to boundedElastic thread which allows blocking operations in order to avoid failures on
        // blocking operations which are performed on repository level.
        Mono.just(exchangeGatewayId)
            .publishOn(Schedulers.boundedElastic())
            .map(id -> {
                var command = new CreateExchangeGatewayAccountCommand(exchangeGatewayId, configuration);
                exchangeGatewayAccountService.execute(command);
                return id;
            })
            .subscribe();
    }

    @DeleteMapping("/{accountId}")
    public void deleteAccount(@PathVariable String exchangeGatewayId,
                              @PathVariable Long accountId)
    {
        var command = new DeleteExchangeGatewayAccountCommand(exchangeGatewayId, accountId);
        exchangeGatewayAccountService.execute(command);
    }
}
