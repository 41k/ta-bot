package root.application.domain.trading;

import lombok.Value;

@Value
public class BarStreamSubscription
{
    Symbol symbol;
    Interval interval;
}
