import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { TabotSharedModule } from 'app/shared/shared.module';
import { STRATEGIES_ROUTE } from './strategies.route';
import { StrategiesComponent } from './strategies.component';
import { ActivationComponent } from './activation.component';

@NgModule({
  imports: [TabotSharedModule, RouterModule.forChild([STRATEGIES_ROUTE])],
  declarations: [StrategiesComponent, ActivationComponent],
})
export class StrategiesModule {}
