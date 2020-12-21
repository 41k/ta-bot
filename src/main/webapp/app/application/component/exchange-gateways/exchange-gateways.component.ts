import { Component } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AddExchangeGatewayComponent } from './add-exchange-gateway.component';
import { ExchangeGatewayApiClient } from '../../api-client/exchange-gateway.api-client';
import { ExchangeGatewayAccountApiClient } from '../../api-client/exchange-gateway-account.api-client';
import { ActivatedExchangeGateway } from '../../model/activated-exchange-gateway.model';
import { NotActivatedExchangeGateway } from '../../model/not-activated-exchange-gateway.model';
import { ExchangeGatewayAccountConfigurationPropertyDictionary } from '../../model/exchange-gateway-account-configuration-property-dictionary.model';

@Component({
  selector: 'jhi-exchange-gateways',
  templateUrl: './exchange-gateways.component.html',
})
export class ExchangeGatewaysComponent {
  notActivatedExchangeGateways!: NotActivatedExchangeGateway[];
  activatedExchangeGateways!: ActivatedExchangeGateway[];

  exchangeGatewayAccountConfigurationPropertyDictionary = ExchangeGatewayAccountConfigurationPropertyDictionary;

  constructor(
    private exchangeGatewayApiClient: ExchangeGatewayApiClient,
    private exchangeGatewayAccountApiClient: ExchangeGatewayAccountApiClient,
    private modalService: NgbModal
  ) {
    this.initialize();
  }

  addExchangeGateway(): void {
    const modalRef = this.modalService.open(AddExchangeGatewayComponent);
    modalRef.componentInstance.initialize(this.notActivatedExchangeGateways);
    modalRef.result.then(() => this.initialize());
  }

  deleteExchangeGateway(exchangeGatewayId: string, exchangeGatewayAccountId: number): void {
    this.exchangeGatewayAccountApiClient
      .deleteExchangeGatewayAccount(exchangeGatewayId, exchangeGatewayAccountId)
      .subscribe(() => this.initialize());
  }

  private initialize(): void {
    this.loadNotActivatedExchangeGateways();
    this.loadActivatedExchangeGateways();
  }

  private loadNotActivatedExchangeGateways(): void {
    this.exchangeGatewayApiClient.getNotActivatedExchangeGateways().subscribe((response: HttpResponse<NotActivatedExchangeGateway[]>) => {
      const exchangeGateways = response.body;
      if (exchangeGateways && exchangeGateways.length > 0) {
        this.notActivatedExchangeGateways = exchangeGateways.sort((g1, g2) => (g1.name > g2.name ? 1 : -1));
      } else {
        this.notActivatedExchangeGateways = [];
      }
    });
  }

  private loadActivatedExchangeGateways(): void {
    this.exchangeGatewayApiClient.getActivatedExchangeGateways().subscribe((response: HttpResponse<ActivatedExchangeGateway[]>) => {
      const exchangeGateways = response.body;
      if (exchangeGateways && exchangeGateways.length > 0) {
        this.activatedExchangeGateways = exchangeGateways.sort((g1, g2) => (g1.name > g2.name ? 1 : -1));
      } else {
        this.activatedExchangeGateways = [];
      }
    });
  }
}
