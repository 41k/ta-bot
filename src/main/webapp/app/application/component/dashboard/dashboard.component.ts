import { Component, ViewChild } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpResponse } from '@angular/common/http';
import { HistoryService } from '../../service/history.service';
import { Trade } from '../../model/trade.model';
import { ChartComponent } from 'ng-apexcharts';

@Component({
  selector: 'jhi-dashboard',
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent {
  private initialTimeRangeLengthInHours = 12;
  private oneMinuteInMillis = 60000;
  private oneHourInMillis = 60 * this.oneMinuteInMillis;
  private initialTimeRangeLengthInMillis = this.initialTimeRangeLengthInHours * this.oneHourInMillis;
  private rangeDateTimeFormat = 'yyyy-MM-ddTHH:mm';
  private chartTimeFormat = 'HH:mm';
  private fractionDigitsCount = 5;

  fromTime = '';
  toTime = '';
  updateViewDataTask?: any;

  nTrades = 0;
  nProfitableTrades = 0;
  nUnprofitableTrades = 0;
  totalProfit = 0;

  @ViewChild('chart') chart!: ChartComponent;
  chartOptions: Partial<any>;

  constructor(private datePipe: DatePipe, private historyService: HistoryService) {
    //this.fromTime = this.datePipe.transform(Date.now() - this.initialTimeRangeLengthInMillis, this.rangeDateTimeFormat)!;
    //this.toTime = this.datePipe.transform(Date.now(), this.rangeDateTimeFormat)!;
    this.fromTime = this.datePipe.transform(new Date(1597901099999).getTime(), this.rangeDateTimeFormat)!;
    this.toTime = this.datePipe.transform(
      new Date(this.fromTime).getTime() + this.initialTimeRangeLengthInMillis,
      this.rangeDateTimeFormat
    )!;
    this.chartOptions = this.getInitialChartOptions();
    this.updateViewData();
    this.runUpdateViewDataTask();
  }

  handleTimeRangeUpdate(): void {
    this.updateViewData();
    this.rerunUpdateViewDataTask();
  }

  private updateViewData(): void {
    this.historyService
      .getTradesHistory({
        fromTimestamp: new Date(this.fromTime).getTime(),
        toTimestamp: new Date(this.toTime).getTime(),
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
      });
  }

  private calculateTotalProfit(trades: Trade[]): number {
    const totalProfit = trades.reduce((accumulator, trade) => accumulator + trade.profit, 0);
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

  private formatFractionDigits(num: number): number {
    return Number(num.toFixed(this.fractionDigitsCount));
  }
}
