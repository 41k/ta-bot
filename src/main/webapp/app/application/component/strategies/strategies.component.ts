import { Component } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ExchangeGatewayApiClient } from '../../api-client/exchange-gateway.api-client';
import { StrategyApiClient } from '../../api-client/strategy.api-client';
import { ActiveStrategy } from '../../model/active-strategy.model';
import { ActivationComponent } from './activation.component';

@Component({
  selector: 'jhi-strategies',
  templateUrl: './strategies.component.html',
})
export class StrategiesComponent {
  private oneMinuteInMillis = 60000;

  exchangeGateways!: string[];
  selectedExchangeGateway!: string;
  activeStrategies!: ActiveStrategy[];
  inactiveStrategies!: string[];

  updateViewDataTask?: any;

  constructor(
    private exchangeGatewaysApiClient: ExchangeGatewayApiClient,
    private strategyApiClient: StrategyApiClient,
    private modalService: NgbModal
  ) {
    this.loadExchangeGateways();
    this.runUpdateViewDataTask();
  }

  loadExchangeGateways(): void {
    this.exchangeGatewaysApiClient.getExchangeGateways().subscribe((response: HttpResponse<string[]>) => {
      const exchangeGateways = response.body;
      if (exchangeGateways && exchangeGateways.length > 0) {
        this.exchangeGateways = exchangeGateways;
        this.selectedExchangeGateway = exchangeGateways[0];
        this.loadStrategies();
      } else {
        this.exchangeGateways = [];
      }
    });
  }

  loadStrategies(): void {
    this.loadActiveStrategies();
    this.loadInactiveStrategies();
  }

  loadActiveStrategies(): void {
    this.strategyApiClient
      .getActiveStrategies({
        exchangeGatewayId: this.selectedExchangeGateway,
      })
      .subscribe((response: HttpResponse<ActiveStrategy[]>) => {
        const activeStrategies = response.body;
        if (activeStrategies && activeStrategies.length > 0) {
          this.activeStrategies = activeStrategies;
        } else {
          this.activeStrategies = [];
        }
      });
  }

  loadInactiveStrategies(): void {
    this.strategyApiClient
      .getInactiveStrategies({
        exchangeGatewayId: this.selectedExchangeGateway,
      })
      .subscribe((response: HttpResponse<string[]>) => {
        const inactiveStrategies = response.body;
        if (inactiveStrategies && inactiveStrategies.length > 0) {
          this.inactiveStrategies = inactiveStrategies;
        } else {
          this.inactiveStrategies = [];
        }
      });
  }

  activateStrategy(strategyId: string): void {
    const modalRef = this.modalService.open(ActivationComponent);
    modalRef.componentInstance.initialize(this.selectedExchangeGateway, strategyId);
    modalRef.result.then(() => this.loadStrategies());
  }

  deactivateStrategy(id: string): void {
    this.strategyApiClient
      .deactivateStrategy({
        exchangeGatewayId: this.selectedExchangeGateway,
        strategyId: id,
      })
      .subscribe(() => this.loadStrategies());
  }

  private runUpdateViewDataTask(): void {
    this.updateViewDataTask = setInterval(() => this.loadStrategies(), this.oneMinuteInMillis);
  }
}
