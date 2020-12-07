package root.application.domain.trading;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TradingOperationContext
{
    String symbol;
    double amount;
    double price;
}
