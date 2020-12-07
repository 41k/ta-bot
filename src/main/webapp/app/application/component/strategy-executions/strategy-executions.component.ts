import { Component } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ExchangeGatewayApiClient } from '../../api-client/exchange-gateway.api-client';
import { StrategyExecutionApiClient } from '../../api-client/strategy-execution.api-client';
import { ExchangeGateway } from '../../model/exchange-gateway.model';
import { StrategyExecution } from '../../model/strategy-execution.model';
import { IntervalDictionary } from '../../model/interval-dictionary.model';

@Component({
  selector: 'jhi-strategy-executions',
  templateUrl: './strategy-executions.component.html',
})
export class StrategyExecutionsComponent {
  private oneMinuteInMillis = 60000;

  exchangeGateways!: ExchangeGateway[];
  selectedExchangeGatewayId!: string;
  strategyExecutions!: StrategyExecution[];

  updateViewDataTask?: any;

  intervalDictionary = IntervalDictionary;
  dateTimeFormat = 'yyyy-MM-dd HH:mm';

  constructor(private exchangeGatewayApiClient: ExchangeGatewayApiClient, private strategyExecutionApiClient: StrategyExecutionApiClient) {
    this.loadExchangeGateways();
    this.runUpdateViewDataTask();
  }

  loadExchangeGateways(): void {
    this.exchangeGatewayApiClient.getExchangeGateways().subscribe((response: HttpResponse<ExchangeGateway[]>) => {
      const exchangeGateways = response.body;
      if (exchangeGateways && exchangeGateways.length > 0) {
        this.exchangeGateways = exchangeGateways;
        this.selectedExchangeGatewayId = exchangeGateways[0].id;
        this.loadStrategyExecutions();
      } else {
        this.exchangeGateways = [];
      }
    });
  }

  loadStrategyExecutions(): void {
    this.strategyExecutionApiClient
      .getStrategyExecutions(this.selectedExchangeGatewayId)
      .subscribe((response: HttpResponse<StrategyExecution[]>) => {
        const strategyExecutions = response.body;
        if (strategyExecutions && strategyExecutions.length > 0) {
          this.strategyExecutions = strategyExecutions;
        } else {
          this.strategyExecutions = [];
        }
      });
  }

  stopStrategyExecution(strategyExecutionId: string): void {
    this.strategyExecutionApiClient
      .stopStrategyExecution(this.selectedExchangeGatewayId, strategyExecutionId)
      .subscribe(() => this.loadStrategyExecutions());
  }

  private runUpdateViewDataTask(): void {
    this.updateViewDataTask = setInterval(() => this.loadStrategyExecutions(), this.oneMinuteInMillis);
  }
}
