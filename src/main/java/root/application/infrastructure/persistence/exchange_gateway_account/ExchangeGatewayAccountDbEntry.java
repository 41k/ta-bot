package root.application.infrastructure.persistence.exchange_gateway_account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Table("exchange_gateway_account")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeGatewayAccountDbEntry implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    @NotNull
    private Long userId;
    @NotNull
    private String exchangeGatewayId;
    @NotNull
    @javax.persistence.Column(columnDefinition = "TEXT")
    private String configuration;
}
