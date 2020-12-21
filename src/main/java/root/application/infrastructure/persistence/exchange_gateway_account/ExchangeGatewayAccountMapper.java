package root.application.infrastructure.persistence.exchange_gateway_account;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jasypt.util.text.TextEncryptor;
import root.application.application.model.ExchangeGatewayAccount;
import root.application.domain.trading.ExchangeGatewayAccountConfigurationProperty;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ExchangeGatewayAccountMapper
{
    private final ObjectMapper objectMapper;
    private final TextEncryptor encryptor;

    public ExchangeGatewayAccount toApplicationModel(ExchangeGatewayAccountDbEntry dbEntry)
    {
        return ExchangeGatewayAccount.builder()
            .id(dbEntry.getId())
            .userId(dbEntry.getUserId())
            .exchangeGatewayId(dbEntry.getExchangeGatewayId())
            .configuration(extractConfiguration(dbEntry))
            .build();
    }

    public ExchangeGatewayAccountDbEntry toDbEntry(ExchangeGatewayAccount applicationModel)
    {
        return ExchangeGatewayAccountDbEntry.builder()
            .id(applicationModel.getId())
            .userId(applicationModel.getUserId())
            .exchangeGatewayId(applicationModel.getExchangeGatewayId())
            .configuration(formConfigurationDbValue(applicationModel))
            .build();
    }

    @SneakyThrows
    private String formConfigurationDbValue(ExchangeGatewayAccount applicationModel)
    {
        var configuration = applicationModel.getConfiguration();
        var configurationJsonString = objectMapper.writeValueAsString(configuration);
        return encryptor.encrypt(configurationJsonString);
    }

    @SneakyThrows
    private Map<ExchangeGatewayAccountConfigurationProperty, String> extractConfiguration(ExchangeGatewayAccountDbEntry dbEntry)
    {
        var encryptedConfigurationJsonString = dbEntry.getConfiguration();
        var configurationJsonString = encryptor.decrypt(encryptedConfigurationJsonString);
        var typeReference = new TypeReference<HashMap<ExchangeGatewayAccountConfigurationProperty,String>>() {};
        return objectMapper.readValue(configurationJsonString, typeReference);
    }
}
