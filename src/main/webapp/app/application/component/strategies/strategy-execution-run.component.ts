import { Component } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { StrategyExecutionApiClient } from '../../api-client/strategy-execution.api-client';
import { Strategy } from '../../model/strategy.model';
import { ExchangeGateway } from '../../model/exchange-gateway.model';
import { ExchangeGatewayApiClient } from '../../api-client/exchange-gateway.api-client';
import { IntervalDictionary } from '../../model/interval-dictionary.model';

@Component({
  selector: 'jhi-strategy-execution-run',
  templateUrl: './strategy-execution-run.component.html',
})
export class StrategyExecutionRunComponent {
  strategy!: Strategy;
  exchangeGateways!: ExchangeGateway[];
  selectedExchangeGatewayId!: string;
  symbols!: string[];
  selectedSymbol!: string;
  amount!: number;
  intervals!: string[];
  selectedInterval!: string;

  intervalDictionary = IntervalDictionary;
  done!: boolean;

  constructor(
    private exchangeGatewayApiClient: ExchangeGatewayApiClient,
    private strategyExecutionApiClient: StrategyExecutionApiClient,
    public activeModal: NgbActiveModal
  ) {}

  initialize(strategy: Strategy): void {
    this.strategy = strategy;
    this.done = false;
    this.loadExchangeGateways();
  }

  loadExchangeGateways(): void {
    this.symbols = [];
    this.selectedSymbol = '';
    this.intervals = [];
    this.selectedInterval = '';
    this.exchangeGatewayApiClient.getExchangeGateways().subscribe((response: HttpResponse<ExchangeGateway[]>) => {
      const exchangeGateways = response.body;
      if (exchangeGateways && exchangeGateways.length > 0) {
        this.exchangeGateways = exchangeGateways;
        this.selectedExchangeGatewayId = exchangeGateways[0].id;
        this.loadSymbols();
      } else {
        this.exchangeGateways = [];
        this.selectedExchangeGatewayId = '';
      }
    });
  }

  loadSymbols(): void {
    this.intervals = [];
    this.selectedInterval = '';
    this.exchangeGatewayApiClient.getSymbols(this.selectedExchangeGatewayId).subscribe((response: HttpResponse<string[]>) => {
      const symbols = response.body;
      if (symbols && symbols.length > 0) {
        this.symbols = symbols;
        this.selectedSymbol = symbols[0];
        this.loadIntervals();
      } else {
        this.symbols = [];
        this.selectedSymbol = '';
      }
    });
  }

  loadIntervals(): void {
    this.exchangeGatewayApiClient.getIntervals(this.selectedExchangeGatewayId).subscribe((response: HttpResponse<string[]>) => {
      const intervals = response.body;
      if (intervals && intervals.length > 0) {
        this.intervals = intervals;
        this.selectedInterval = intervals[0];
      } else {
        this.intervals = [];
        this.selectedInterval = '';
      }
    });
  }

  runStrategyExecution(): void {
    if (!this.selectedExchangeGatewayId || !this.selectedSymbol || !this.amount || !this.selectedInterval) {
      return;
    }
    this.strategyExecutionApiClient
      .runStrategyExecution(this.selectedExchangeGatewayId, {
        strategyId: this.strategy.id,
        symbol: this.selectedSymbol,
        amount: this.amount,
        interval: this.selectedInterval,
      })
      .subscribe(() => (this.done = true));
  }
}
