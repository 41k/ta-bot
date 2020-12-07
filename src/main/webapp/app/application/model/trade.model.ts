import { Tick } from './tick.model';

export class Trade {
  constructor(
    public exchangeGateway: string,
    public strategyExecutionId: string,
    public strategyName: string,
    public symbol: string,
    public amount: number,
    public totalProfit: number,
    public absoluteProfit: number,
    public interval: string,
    public entryTimestamp: number,
    public exitTimestamp: number,
    public ticks: Array<Tick>
  ) {}
}
