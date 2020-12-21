import { Component } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { StrategyExecutionApiClient } from '../../api-client/strategy-execution.api-client';
import { Strategy } from '../../model/strategy.model';
import { ExchangeGatewayApiClient } from '../../api-client/exchange-gateway.api-client';
import { IntervalDictionary } from '../../model/interval-dictionary.model';
import { SymbolDictionary } from '../../model/symbol-dictionary.model';
import { ActivatedExchangeGateway } from '../../model/activated-exchange-gateway.model';

@Component({
  selector: 'jhi-strategy-execution-run',
  templateUrl: './strategy-execution-run.component.html',
})
export class StrategyExecutionRunComponent {
  strategy!: Strategy;
  exchangeGateways!: ActivatedExchangeGateway[];
  selectedExchangeGatewayId!: string;
  symbols!: string[];
  selectedSymbol!: string;
  amount!: number;
  intervals!: string[];
  selectedInterval!: string;

  symbolDictionary = SymbolDictionary;
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
    this.exchangeGatewayApiClient.getActivatedExchangeGateways().subscribe((response: HttpResponse<ActivatedExchangeGateway[]>) => {
      const exchangeGateways = response.body;
      if (exchangeGateways && exchangeGateways.length > 0) {
        this.exchangeGateways = exchangeGateways.sort((g1, g2) => (g1.name > g2.name ? 1 : -1));
        this.selectedExchangeGatewayId = exchangeGateways[0].id;
        this.loadSymbols();
      } else {
        this.exchangeGateways = [];
      }
    });
  }

  loadSymbols(): void {
    this.intervals = [];
    this.selectedInterval = '';
    this.exchangeGatewayApiClient.getSymbols(this.selectedExchangeGatewayId).subscribe((response: HttpResponse<string[]>) => {
      const symbols = response.body;
      if (symbols && symbols.length > 0) {
        this.symbols = symbols.sort((s1, s2) => (s1 > s2 ? 1 : -1));
        this.selectedSymbol = this.symbols[0];
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
        this.selectedInterval = this.intervals[0];
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
    const exchangeGateway = this.exchangeGateways.filter(gateway => gateway.id === this.selectedExchangeGatewayId)[0];
    this.strategyExecutionApiClient
      .runStrategyExecution(exchangeGateway.id, exchangeGateway.accountId, {
        strategyId: this.strategy.id,
        symbol: this.selectedSymbol,
        amount: this.amount,
        interval: this.selectedInterval,
      })
      .subscribe(() => (this.done = true));
  }
}
