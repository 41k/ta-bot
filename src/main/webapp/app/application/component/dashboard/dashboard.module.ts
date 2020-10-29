import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { TabotSharedModule } from 'app/shared/shared.module';
import { DASHBOARD_ROUTE } from './dashboard.route';
import { DashboardComponent } from './dashboard.component';
import { NgApexchartsModule } from 'ng-apexcharts';

@NgModule({
  imports: [TabotSharedModule, NgApexchartsModule, RouterModule.forChild([DASHBOARD_ROUTE])],
  declarations: [DashboardComponent],
})
export class DashboardModule {}
