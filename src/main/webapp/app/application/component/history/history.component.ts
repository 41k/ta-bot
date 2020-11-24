import { Component, ViewChild } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpResponse } from '@angular/common/http';
import { HistoryApiClient } from '../../api-client/history.api-client';
import { Trade } from '../../model/trade.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TradeComponent } from '../trade/trade.component';
import { ChartComponent } from 'ng-apexcharts';

@Component({
  selector: 'jhi-history',
  templateUrl: './history.component.html',
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

  strategies!: string[];
  selectedStrategy!: string;

  nTrades = 0;
  nProfitableTrades = 0;
  nUnprofitableTrades = 0;
  totalProfit = 0;

  trades!: Map<number, Trade>;

  tradesTableDateTimeFormat = 'yyyy-MM-dd HH:mm';

  @ViewChild('chart') chart!: ChartComponent;
  chartOptions: Partial<any>;

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
    this.strategies = [];
    this.trades = new Map<number, Trade>();
    this.historyApiClient
      .getExchangeGateways({
        fromTimestamp: new Date(this.fromTime).getTime(),
        toTimestamp: new Date(this.toTime).getTime(),
      })
      .subscribe((response: HttpResponse<string[]>) => {
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
    this.trades = new Map<number, Trade>();
    this.historyApiClient
      .getStrategies({
        fromTimestamp: new Date(this.fromTime).getTime(),
        toTimestamp: new Date(this.toTime).getTime(),
        exchangeGatewayId: this.selectedExchangeGateway,
      })
      .subscribe((response: HttpResponse<string[]>) => {
        const strategies = response.body;
        if (strategies && strategies.length > 0) {
          this.strategies = strategies;
          this.selectedStrategy = strategies[0];
          this.loadTrades();
        } else {
          this.strategies = [];
        }
      });
  }

  loadTrades(): void {
    this.historyApiClient
      .getTrades({
        fromTimestamp: new Date(this.fromTime).getTime(),
        toTimestamp: new Date(this.toTime).getTime(),
        exchangeGatewayId: this.selectedExchangeGateway,
        strategyId: this.selectedStrategy,
        page: 0,
        size: 0,
        sort: ['fromTimestamp,asc'],
      })
      .subscribe((response: HttpResponse<Trade[]>) => {
        const trades = response.body;
        if (trades == null) {
          return;
        }
        this.nTrades = trades.length;
        this.nProfitableTrades = trades.filter(trade => trade.profit >= 0).length;
        this.nUnprofitableTrades = trades.filter(trade => trade.profit < 0).length;
        this.totalProfit = this.calculateTotalProfit(trades);
        this.updateChart(trades);
        this.setTrades(trades);
      });
  }

  private initTimeRangeFilter(): void {
    this.fromTime = this.datePipe.transform(new Date(1597901099999).getTime(), this.rangeDateTimeFormat)!;
    this.toTime = this.datePipe.transform(
      new Date(this.fromTime).getTime() + this.initialTimeRangeLengthInMillis,
      this.rangeDateTimeFormat
    )!;
  }

  private calculateTotalProfit(trades: Trade[]): number {
    const totalProfit = trades.reduce((accumulator, trade) => accumulator + trade.profit, 0);
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
      profit = this.formatFractionDigits(profit + trade.profit);
      data.push(profit);
      const label = this.datePipe.transform(new Date(trade.exitTimestamp).getTime(), this.chartTimeFormat)!;
      labels.push(label);
    }
    this.chartOptions.series[0].data = data;
    this.chartOptions.labels = labels;
  }
}
