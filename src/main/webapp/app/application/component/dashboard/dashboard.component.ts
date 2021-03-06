import { Component, ViewChild, OnDestroy } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpResponse } from '@angular/common/http';
import { HistoryApiClient } from '../../api-client/history.api-client';
import { Trade } from '../../model/trade.model';
import { ChartComponent } from 'ng-apexcharts';

@Component({
  selector: 'jhi-dashboard',
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnDestroy {
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
  updateViewDataTask?: any;

  nTrades = 0;
  nProfitableTrades = 0;
  nUnprofitableTrades = 0;
  totalProfit = 0;

  @ViewChild('chart') chart!: ChartComponent;
  chartOptions: Partial<any>;

  constructor(private datePipe: DatePipe, private historyApiClient: HistoryApiClient) {
    this.chartOptions = this.getInitialChartOptions();
    this.initFilters();
    this.loadExchangeGateways();
    this.runUpdateViewDataTask();
  }

  handleTimeRangeUpdate(): void {
    this.loadExchangeGateways();
    this.rerunUpdateViewDataTask();
  }

  ngOnDestroy(): void {
    clearInterval(this.updateViewDataTask);
  }

  private initFilters(): void {
    this.fromTime = this.datePipe.transform(Date.now() - this.initialTimeRangeLengthInMillis, this.rangeDateTimeFormat)!;
    this.toTime = this.datePipe.transform(Date.now(), this.rangeDateTimeFormat)!;
    // this.fromTime = this.datePipe.transform(new Date(1597901099999).getTime(), this.rangeDateTimeFormat)!;
    // this.toTime = this.datePipe.transform(
    //   new Date(this.fromTime).getTime() + this.initialTimeRangeLengthInMillis,
    //   this.rangeDateTimeFormat
    // )!;
  }

  private loadExchangeGateways(): void {
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
          this.updateViewData();
        } else {
          this.exchangeGateways = [];
        }
      });
  }

  updateViewData(): void {
    this.historyApiClient
      .getTrades({
        fromTimestamp: new Date(this.fromTime).getTime(),
        toTimestamp: new Date(this.toTime).getTime(),
        exchangeGateway: this.selectedExchangeGateway,
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
      });
  }

  private calculateTotalProfit(trades: Trade[]): number {
    const totalProfit = trades.reduce((accumulator, trade) => accumulator + trade.totalProfit, 0);
    return this.formatFractionDigits(totalProfit);
  }

  private runUpdateViewDataTask(): void {
    this.updateViewDataTask = setInterval(() => {
      this.shiftTimeRange();
      this.updateViewData();
    }, this.oneMinuteInMillis);
  }

  private shiftTimeRange(): void {
    const incrementedFromTimeInMillis = new Date(this.fromTime).getTime() + this.oneMinuteInMillis;
    this.fromTime = this.datePipe.transform(incrementedFromTimeInMillis, this.rangeDateTimeFormat)!;
    const incrementedToTimeInMillis = new Date(this.toTime).getTime() + this.oneMinuteInMillis;
    this.toTime = this.datePipe.transform(incrementedToTimeInMillis, this.rangeDateTimeFormat)!;
  }

  private rerunUpdateViewDataTask(): void {
    clearInterval(this.updateViewDataTask);
    this.runUpdateViewDataTask();
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
        height: 330,
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

  private formatFractionDigits(num: number): number {
    return Number(num.toFixed(this.fractionDigitsCount));
  }
}
