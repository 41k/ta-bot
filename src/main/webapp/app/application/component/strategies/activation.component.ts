import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { StrategyApiClient } from '../../api-client/strategy.api-client';

@Component({
  selector: 'jhi-strategy-activation',
  templateUrl: './activation.component.html',
})
export class ActivationComponent {
  private exchangeGatewayId!: string;
  strategyId!: string;

  amountToBuy?: number;

  constructor(public activeModal: NgbActiveModal, private strategyApiClient: StrategyApiClient) {}

  initialize(exchangeGatewayId: string, strategyId: string): void {
    this.exchangeGatewayId = exchangeGatewayId;
    this.strategyId = strategyId;
  }

  activateStrategy(): void {
    if (!this.amountToBuy) {
      return;
    }
    this.strategyApiClient
      .activateStrategy({
        exchangeGatewayId: this.exchangeGatewayId,
        strategyId: this.strategyId,
        amount: this.amountToBuy,
      })
      .subscribe(() => this.activeModal.close());
  }
}
