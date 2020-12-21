import { Component } from '@angular/core';
import { NotActivatedExchangeGateway } from '../../model/not-activated-exchange-gateway.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ExchangeGatewayApiClient } from '../../api-client/exchange-gateway.api-client';
import { ExchangeGatewayAccountApiClient } from '../../api-client/exchange-gateway-account.api-client';
import { HttpResponse } from '@angular/common/http';
import { ExchangeGatewayAccountConfigurationPropertyDictionary } from '../../model/exchange-gateway-account-configuration-property-dictionary.model';

@Component({
  selector: 'jhi-add-exchange-gateway',
  templateUrl: './add-exchange-gateway.component.html',
})
export class AddExchangeGatewayComponent {
  exchangeGateways!: NotActivatedExchangeGateway[];
  selectedExchangeGatewayId!: string;
  accountConfiguration!: Map<string, string>;

  exchangeGatewayAccountConfigurationPropertyDictionary = ExchangeGatewayAccountConfigurationPropertyDictionary;

  constructor(
    private exchangeGatewayApiClient: ExchangeGatewayApiClient,
    private exchangeGatewayAccountApiClient: ExchangeGatewayAccountApiClient,
    private activeModal: NgbActiveModal
  ) {}

  initialize(exchangeGateways: NotActivatedExchangeGateway[]): void {
    this.exchangeGateways = exchangeGateways;
    this.selectedExchangeGatewayId = exchangeGateways[0].id;
    this.updateAccountConfiguration();
  }

  updateAccountConfiguration(): void {
    this.exchangeGatewayApiClient
      .getAccountConfigurationProperties(this.selectedExchangeGatewayId)
      .subscribe((response: HttpResponse<string[]>) => {
        const accountConfigurationProperties = response.body;
        if (accountConfigurationProperties && accountConfigurationProperties.length > 0) {
          const sortedProperties = accountConfigurationProperties.sort((p1, p2) => (p1 > p2 ? 1 : -1));
          const configuration = new Map<string, string>();
          sortedProperties.forEach(property => configuration.set(property, ''));
          this.accountConfiguration = configuration;
        } else {
          this.accountConfiguration = new Map<string, string>();
        }
      });
  }

  addExchangeGateway(): void {
    this.exchangeGatewayAccountApiClient
      .createExchangeGatewayAccount(this.selectedExchangeGatewayId, this.accountConfiguration)
      .subscribe(() => this.activeModal.close());
  }
}
