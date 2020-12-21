package root.application.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Data
@Validated
@ConfigurationProperties
public class ExchangeGatewaysConfigurationProperties
{
    @NotEmpty
    private Map<String, ExchangeGatewayConfiguration> exchangeGateways;

    public ExchangeGatewayConfiguration getExchangeGatewayConfiguration(String key)
    {
        return ofNullable(exchangeGateways.get(key)).orElseThrow(
            () -> new NoSuchElementException(format("Exchange gateway configuration is not found for key [%s].", key)));
    }
}
