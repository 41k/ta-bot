import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { TabotSharedModule } from 'app/shared/shared.module';
import { HISTORY_ROUTE } from './history.route';
import { HistoryComponent } from './history.component';
import { NgApexchartsModule } from 'ng-apexcharts';
import { TradeComponent } from '../trade/trade.component';

@NgModule({
  imports: [TabotSharedModule, NgApexchartsModule, RouterModule.forChild([HISTORY_ROUTE])],
  declarations: [HistoryComponent, TradeComponent],
})
export class HistoryModule {}
