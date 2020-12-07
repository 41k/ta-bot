import { Component, ViewChild } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ChartComponent } from 'ng-apexcharts';
import { DatePipe } from '@angular/common';
import { Trade } from '../../model/trade.model';
import { Tick } from '../../model/tick.model';
import { Level } from '../../model/level.model';
import { IntervalDictionary } from '../../model/interval-dictionary.model';

@Component({
  selector: 'jhi-trade-chart',
  templateUrl: './trade.component.html',
})
export class TradeComponent {
  private additionalChartIndicatorTypes: string[] = ['RSI', 'MACD', 'OBV'];
  private chartTimeFormat = 'HH:mm:ss';

  intervalDictionary = IntervalDictionary;
  trade!: Trade;

  @ViewChild('chart') chart!: ChartComponent;
  mainChartOptions!: Partial<any>;
  additionalChartsOptions: Partial<any>[] = [];

  constructor(public activeModal: NgbActiveModal, private datePipe: DatePipe) {}

  initialize(trade: Trade): void {
    this.trade = trade;
    this.initMainChart(trade);
    this.renderAdditionalCharts(trade);
  }

  private initMainChart(trade: Trade): void {
    const chartOptions = this.getInitialMainChartOptions();
    const ticks = trade.ticks;
    this.addLineIndicators(chartOptions, ticks);
    this.addBarSeries(chartOptions, ticks);
    this.addSignals(chartOptions, ticks);
    this.addLevels(chartOptions, ticks);
    this.mainChartOptions = chartOptions;
  }

  private getInitialMainChartOptions(): Partial<any> {
    return {
      series: [],
      annotations: {
        yaxis: [],
        xaxis: [],
      },
      chart: {
        height: 350,
        type: 'line',
        toolbar: {
          show: false,
        },
      },
      title: {
        text: 'MAIN',
        align: 'center',
      },
      stroke: {
        width: [],
      },
      xaxis: {
        tooltip: {
          enabled: true,
          offsetY: 40,
          formatter: (timestamp: number) => this.datePipe.transform(timestamp, this.chartTimeFormat)!,
        },
        type: 'datetime',
      },
      yaxis: {
        opposite: true,
        labels: {
          formatter: (val: number) => val.toFixed(2),
        },
      },
      plotOptions: {
        candlestick: {
          colors: {
            upward: '#1ab394',
            downward: '#ed5565',
          },
        },
      },
    };
  }

  private addLineIndicators(chartOptions: any, ticks: Tick[]): void {
    const indicatorNameToSeriesMap = {};
    for (const indicator of Object.entries(ticks[0].mainChartNumIndicators)) {
      const indicatorName = indicator[0];
      const series = {
        name: indicatorName,
        type: 'line',
        data: [],
      };
      indicatorNameToSeriesMap[indicatorName] = series;
    }
    ticks.forEach(tick => {
      for (const indicator of Object.entries(tick.mainChartNumIndicators)) {
        const indicatorName = indicator[0];
        const indicatorValue = indicator[1];
        indicatorNameToSeriesMap[indicatorName].data.push({
          x: new Date(tick.timestamp),
          y: indicatorValue,
        });
      }
    });
    for (const entry of Object.entries(indicatorNameToSeriesMap)) {
      chartOptions.series.push(entry[1]);
      chartOptions.stroke.width.push(2);
    }
  }

  private addBarSeries(chartOptions: any, ticks: Tick[]): void {
    const series: { name: string; type: string; data: any[] } = {
      name: 'Close Price',
      type: 'candlestick',
      data: [],
    };
    ticks.forEach(tick => {
      const bar = {
        x: new Date(tick.timestamp),
        y: [tick.open, tick.high, tick.low, tick.close],
      };
      series.data.push(bar);
    });
    chartOptions.series.push(series);
    chartOptions.stroke.width.push(1);
  }

  private addSignals(chartOptions: any, ticks: Tick[]): void {
    ticks.forEach(tick => {
      if (tick.signal) {
        const signal = this.createSignal(tick);
        chartOptions.annotations.xaxis.push(signal);
      }
    });
  }

  private createSignal(tick: Tick): any {
    const name = tick.signal;
    const timestamp = tick.timestamp;
    const colorHex = name === 'BUY' ? '#ed5565' : '#1ab394';
    return {
      x: timestamp,
      strokeDashArray: 0,
      borderColor: colorHex,
      yAxisIndex: 0,
      label: {
        show: true,
        text: name,
        offsetX: 8,
        borderWidth: 0,
        style: {
          color: '#fff',
          background: colorHex,
        },
      },
    };
  }

  private addLevels(chartOptions: any, ticks: Tick[]): void {
    ticks.forEach(tick => {
      const levels = tick.levels;
      if (levels) {
        levels.forEach(level => {
          chartOptions.annotations.yaxis.push(this.createLevel(level));
        });
      }
    });
  }

  private createLevel(level: Level): any {
    const colorHex = '#1ab394';
    return {
      y: level.value,
      strokeDashArray: 0,
      borderColor: colorHex,
      label: {
        borderColor: colorHex,
        style: {
          color: '#fff',
          background: colorHex,
        },
        text: level.name,
      },
    };
  }

  private renderAdditionalCharts(trade: Trade): void {
    const ticks = trade.ticks;
    const indicatorNameToSeriesMap = this.formAdditionalChartNumIndicatorNameToSeriesMap(ticks);
    this.additionalChartIndicatorTypes.forEach(indicatorType => {
      this.renderAdditionalChart(indicatorType, indicatorNameToSeriesMap, ticks);
    });
  }

  private formAdditionalChartNumIndicatorNameToSeriesMap(ticks: Tick[]): any {
    const indicatorNameToSeriesMap = {};
    for (const indicator of Object.entries(ticks[0].additionalChartNumIndicators)) {
      const indicatorName = indicator[0];
      const series = {
        name: indicatorName,
        data: [],
      };
      indicatorNameToSeriesMap[indicatorName] = series;
    }
    ticks.forEach(tick => {
      for (const indicator of Object.entries(tick.additionalChartNumIndicators)) {
        const indicatorName = indicator[0];
        const indicatorValue = indicator[1];
        indicatorNameToSeriesMap[indicatorName].data.push({
          x: new Date(tick.timestamp),
          y: indicatorValue,
        });
      }
    });
    return indicatorNameToSeriesMap;
  }

  private renderAdditionalChart(indicatorType: string, indicatorNameToSeriesMap: any, ticks: Tick[]): void {
    const indicatorTypeSeries = this.getSeriesForIndicatorType(indicatorType, indicatorNameToSeriesMap);
    if (Object.keys(indicatorTypeSeries).length === 0) {
      return;
    }
    const chartOptions = this.getInitialAdditionalChartOptions(indicatorType);
    this.addSeriesToAdditionalChart(chartOptions, indicatorTypeSeries);
    this.addSignals(chartOptions, ticks);
    this.addXAxisTimeLabels(chartOptions, ticks);
    this.additionalChartsOptions.push(chartOptions);
  }

  private getSeriesForIndicatorType(indicatorType: string, indicatorNameToSeriesMap: any): any[] {
    const indicatorTypeSeries = [];
    for (const entry of Object.entries(indicatorNameToSeriesMap)) {
      const indicatorName = entry[0];
      const series = entry[1];
      if (indicatorName.startsWith(indicatorType)) {
        indicatorTypeSeries.push(series);
      }
    }
    return indicatorTypeSeries;
  }

  private getInitialAdditionalChartOptions(chartTitle: string): Partial<any> {
    return {
      chart: {
        height: 250,
        type: 'line',
        toolbar: {
          show: false,
        },
      },
      series: [],
      annotations: {
        xaxis: [],
      },
      dataLabels: {
        enabled: false,
      },
      stroke: {
        curve: 'straight',
        width: 2,
      },
      title: {
        text: chartTitle,
        align: 'center',
      },
      labels: [],
      xaxis: {
        tooltip: {
          enabled: true,
          offsetY: 40,
          formatter: (timestamp: number) => this.datePipe.transform(timestamp, this.chartTimeFormat)!,
        },
        type: 'datetime',
      },
      yaxis: {
        opposite: true,
        labels: {
          formatter: (val: number) => val.toFixed(2),
        },
      },
      grid: {
        padding: {
          left: 30,
          right: 40,
        },
      },
    };
  }

  private addSeriesToAdditionalChart(chartOptions: any, indicatorTypeSeries: any[]): void {
    indicatorTypeSeries.forEach(series => {
      chartOptions.series.push(series);
    });
  }

  private addXAxisTimeLabels(chartOptions: any, ticks: Tick[]): void {
    ticks.forEach(tick => {
      chartOptions.labels.push(tick.timestamp);
    });
  }
}
