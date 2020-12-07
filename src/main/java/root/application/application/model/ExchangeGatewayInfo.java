package root.application.application.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ExchangeGatewayInfo
{
    String id;
    String name;
}
