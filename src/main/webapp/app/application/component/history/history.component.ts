import { Component, ViewChild } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpResponse } from '@angular/common/http';
import { HistoryApiClient } from '../../api-client/history.api-client';
import { Trade } from '../../model/trade.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TradeComponent } from '../trade/trade.component';
import { ChartComponent } from 'ng-apexcharts';
import { StrategyExecution } from '../../model/strategy-execution.model';
import { IntervalDictionary } from '../../model/interval-dictionary.model';
import { SymbolDictionary } from '../../model/symbol-dictionary.model';

@Component({
  selector: 'jhi-history',
  templateUrl: './history.component.html',
  styleUrls: ['history.scss'],
})
export class HistoryComponent {
  private initialTimeRangeLengthInHours = 12;
  private oneMinuteInMillis = 60000;
  private oneHourInMillis = 60 * this.oneMinuteInMillis;
  private initialTimeRangeLengthInMillis = this.initialTimeRangeLengthInHours * this.oneHourInMillis;
  private rangeDateTimeFormat = 'yyyy-MM-ddTHH:mm';
  private chartTimeFormat = 'HH:mm';
  private fractionDigitsCount = 5;

  fromTime = '';
  toTime = '';

  exchangeGateways!: string[];
  selectedExchangeGateway!: string;

  strategyExecutions!: StrategyExecution[];
  selectedStrategyExecutionId!: string;

  nTrades = 0;
  nProfitableTrades = 0;
  nUnprofitableTrades = 0;
  totalProfit = 0;

  trades!: Map<number, Trade>;

  tradesTableDateTimeFormat = 'yyyy-MM-dd HH:mm';

  @ViewChild('chart') chart!: ChartComponent;
  chartOptions: Partial<any>;

  symbolDictionary = SymbolDictionary;
  intervalDictionary = IntervalDictionary;

  constructor(private datePipe: DatePipe, private historyApiClient: HistoryApiClient, private modalService: NgbModal) {
    this.initTimeRangeFilter();
    this.loadExchangeGateways();
    this.chartOptions = this.getInitialChartOptions();
  }

  showTradeDetails(entryTimestamp: number): void {
    const trade = this.trades.get(entryTimestamp);
    const modalRef = this.modalService.open(TradeComponent, { size: 'xl' });
    modalRef.componentInstance.initialize(trade);
  }

  loadExchangeGateways(): void {
    this.strategyExecutions = [];
    this.trades = new Map<number, Trade>();
    this.historyApiClient
      .getExchangeGateways({
        fromTimestamp: new Date(this.fromTime).getTime(),
        toTimestamp: new Date(this.toTime).getTime(),
      })
      .subscribe((response: HttpResponse<string[]>) => {
        const exchangeGateways = response.body;
        if (exchangeGateways && exchangeGateways.length > 0) {
          this.exchangeGateways = exchangeGateways.sort((g1, g2) => (g1 > g2 ? 1 : -1));
          this.selectedExchangeGateway = this.exchangeGateways[0];
          this.loadStrategyExecutions();
        } else {
          this.exchangeGateways = [];
        }
      });
  }

  loadStrategyExecutions(): void {
    this.trades = new Map<number, Trade>();
    this.historyApiClient
      .getStrategyExecutions({
        fromTimestamp: new Date(this.fromTime).getTime(),
        toTimestamp: new Date(this.toTime).getTime(),
        exchangeGateway: this.selectedExchangeGateway,
      })
      .subscribe((response: HttpResponse<StrategyExecution[]>) => {
        const strategyExecutions = response.body;
        if (strategyExecutions && strategyExecutions.length > 0) {
          this.strategyExecutions = strategyExecutions.sort((e1, e2) => (e1.strategyName > e2.strategyName ? 1 : -1));
          this.selectedStrategyExecutionId = this.strategyExecutions[0].id;
          this.loadTrades();
        } else {
          this.strategyExecutions = [];
        }
      });
  }

  loadTrades(): void {
    this.historyApiClient
      .getTrades({
        fromTimestamp: new Date(this.fromTime).getTime(),
        toTimestamp: new Date(this.toTime).getTime(),
        exchangeGateway: this.selectedExchangeGateway,
        strategyExecutionId: this.selectedStrategyExecutionId,
        page: 0,
        size: 10000,
        sort: ['exitTimestamp,asc'],
      })
      .subscribe((response: HttpResponse<Trade[]>) => {
        const trades = response.body;
        if (trades == null) {
          return;
        }
        this.nTrades = trades.length;
        this.nProfitableTrades = trades.filter(trade => trade.totalProfit >= 0).length;
        this.nUnprofitableTrades = trades.filter(trade => trade.totalProfit < 0).length;
        this.totalProfit = this.calculateTotalProfit(trades);
        this.updateChart(trades);
        this.setTrades(trades);
      });
  }

  private initTimeRangeFilter(): void {
    this.fromTime = this.datePipe.transform(Date.now() - this.initialTimeRangeLengthInMillis, this.rangeDateTimeFormat)!;
    this.toTime = this.datePipe.transform(Date.now(), this.rangeDateTimeFormat)!;
    // this.fromTime = this.datePipe.transform(new Date(1597901099999).getTime(), this.rangeDateTimeFormat)!;
    // this.toTime = this.datePipe.transform(
    //   new Date(this.fromTime).getTime() + this.initialTimeRangeLengthInMillis,
    //   this.rangeDateTimeFormat
    // )!;
  }

  private calculateTotalProfit(trades: Trade[]): number {
    const totalProfit = trades.reduce((accumulator, trade) => accumulator + trade.totalProfit, 0);
    return this.formatFractionDigits(totalProfit);
  }

  private formatFractionDigits(num: number): number {
    return Number(num.toFixed(this.fractionDigitsCount));
  }

  private setTrades(trades: Trade[]): void {
    const entryTimestampToTradeMap = new Map<number, Trade>();
    trades.forEach(trade => {
      entryTimestampToTradeMap.set(trade.entryTimestamp, trade);
    });
    this.trades = entryTimestampToTradeMap;
  }

  private getInitialChartOptions(): Partial<any> {
    return {
      series: [
        {
          name: 'Total profit',
          data: [],
        },
      ],
      chart: {
        type: 'area',
        height: 350,
        zoom: {
          enabled: false,
        },
        toolbar: {
          show: false,
        },
      },
      dataLabels: {
        enabled: false,
      },
      stroke: {
        curve: 'straight',
      },
      labels: [],
      xaxis: {
        type: 'category',
      },
      yaxis: {
        opposite: true,
      },
      legend: {
        horizontalAlign: 'left',
      },
    };
  }

  private updateChart(trades: Trade[]): void {
    let profit = 0;
    const data = [];
    const labels = [];
    for (let i = 0; i < trades.length; i++) {
      const trade = trades[i];
      profit = this.formatFractionDigits(profit + trade.totalProfit);
      data.push(profit);
      const label = this.datePipe.transform(new Date(trade.exitTimestamp).getTime(), this.chartTimeFormat)!;
      labels.push(label);
    }
    this.chartOptions.series[0].data = data;
    this.chartOptions.labels = labels;
  }
}
