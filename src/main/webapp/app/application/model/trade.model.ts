import { Tick } from './tick.model';

export class Trade {
  constructor(
    public strategyId: string,
    public exchangeId: string,
    public entryTimestamp: number,
    public exitTimestamp: number,
    public profit: number,
    public ticks: Array<Tick>
  ) {}
}
