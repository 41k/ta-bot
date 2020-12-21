package root.application.infrastructure.exchange_gateway.currency_com;

import lombok.Data;

import java.util.Map;

@Data
public class WssIncomingMessage
{
    private static final String OK_STATUS = "OK";
    private static final String OHLC_MESSAGE_TYPE = "ohlc.event";

    String status;
    String destination;
    Map<String, Object> payload;

    public boolean hasOkStatus()
    {
        return OK_STATUS.equals(status);
    }

    public boolean isOhlcMessage()
    {
        return OHLC_MESSAGE_TYPE.equals(destination);
    }
}
