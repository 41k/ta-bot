import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { TabotSharedModule } from 'app/shared/shared.module';
import { STRATEGY_EXECUTIONS_ROUTE } from './strategy-executions.route';
import { StrategyExecutionsComponent } from './strategy-executions.component';

@NgModule({
  imports: [TabotSharedModule, RouterModule.forChild([STRATEGY_EXECUTIONS_ROUTE])],
  declarations: [StrategyExecutionsComponent],
})
export class StrategyExecutionsModule {}
