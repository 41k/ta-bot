import { StrategyExecutionStatistics } from './strategy-execution-statistics.model';

export class StrategyExecution {
  constructor(
    public id: string,
    public startTime: number,
    public status: string,
    public strategyName: string,
    public symbol: string,
    public amount: number,
    public interval: string,
    public statistics: StrategyExecutionStatistics
  ) {}
}
