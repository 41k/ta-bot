package root.application.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.application.application.ExchangeGatewayService;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/exchange-gateways")
@RequiredArgsConstructor
public class ExchangeGatewayApiController
{
    private final ExchangeGatewayService exchangeGatewayService;

    @GetMapping
    public Set<String> getIds()
    {
        return exchangeGatewayService.getIds();
    }
}
