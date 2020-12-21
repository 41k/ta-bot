import { Component, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ExchangeGatewayApiClient } from '../../api-client/exchange-gateway.api-client';
import { StrategyExecutionApiClient } from '../../api-client/strategy-execution.api-client';
import { StrategyExecution } from '../../model/strategy-execution.model';
import { IntervalDictionary } from '../../model/interval-dictionary.model';
import { SymbolDictionary } from '../../model/symbol-dictionary.model';
import { ActivatedExchangeGateway } from '../../model/activated-exchange-gateway.model';

@Component({
  selector: 'jhi-strategy-executions',
  templateUrl: './strategy-executions.component.html',
})
export class StrategyExecutionsComponent implements OnDestroy {
  private oneMinuteInMillis = 60000;

  exchangeGateways!: ActivatedExchangeGateway[];
  selectedExchangeGatewayId!: string;
  strategyExecutions!: StrategyExecution[];

  updateViewDataTask?: any;

  symbolDictionary = SymbolDictionary;
  intervalDictionary = IntervalDictionary;
  dateTimeFormat = 'yyyy-MM-dd HH:mm';

  constructor(private exchangeGatewayApiClient: ExchangeGatewayApiClient, private strategyExecutionApiClient: StrategyExecutionApiClient) {
    this.loadExchangeGateways();
    this.runUpdateViewDataTask();
  }

  loadExchangeGateways(): void {
    this.exchangeGatewayApiClient.getActivatedExchangeGateways().subscribe((response: HttpResponse<ActivatedExchangeGateway[]>) => {
      const exchangeGateways = response.body;
      if (exchangeGateways && exchangeGateways.length > 0) {
        this.exchangeGateways = exchangeGateways.sort((g1, g2) => (g1.name > g2.name ? 1 : -1));
        this.selectedExchangeGatewayId = exchangeGateways[0].id;
        this.loadStrategyExecutions();
      } else {
        this.exchangeGateways = [];
      }
    });
  }

  loadStrategyExecutions(): void {
    const exchangeGateway = this.getSelectedExchangeGateway();
    this.strategyExecutionApiClient
      .getStrategyExecutions(exchangeGateway.id, exchangeGateway.accountId)
      .subscribe((response: HttpResponse<StrategyExecution[]>) => {
        const strategyExecutions = response.body;
        if (strategyExecutions && strategyExecutions.length > 0) {
          this.strategyExecutions = strategyExecutions.sort((e1, e2) => (e1.strategyName > e2.strategyName ? 1 : -1));
        } else {
          this.strategyExecutions = [];
        }
      });
  }

  stopStrategyExecution(strategyExecutionId: string): void {
    const exchangeGateway = this.getSelectedExchangeGateway();
    this.strategyExecutionApiClient
      .stopStrategyExecution(exchangeGateway.id, exchangeGateway.accountId, strategyExecutionId)
      .subscribe(() => this.loadStrategyExecutions());
  }

  ngOnDestroy(): void {
    clearInterval(this.updateViewDataTask);
  }

  private getSelectedExchangeGateway(): ActivatedExchangeGateway {
    return this.exchangeGateways.filter(gateway => gateway.id === this.selectedExchangeGatewayId)[0];
  }

  private runUpdateViewDataTask(): void {
    this.updateViewDataTask = setInterval(() => this.loadStrategyExecutions(), this.oneMinuteInMillis);
  }
}
