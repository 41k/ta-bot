import { MainChartLevel } from './main-chart-level.model';

export class Tick {
  constructor(
    public open: number,
    public high: number,
    public low: number,
    public close: number,
    public volume: number,
    public timestamp: number,
    public signal: string,
    public mainChartLevels: Array<MainChartLevel>,
    public mainChartNumIndicators: any,
    public additionalChartNumIndicators: any
  ) {}
}
