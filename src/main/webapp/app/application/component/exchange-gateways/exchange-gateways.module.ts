import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { TabotSharedModule } from 'app/shared/shared.module';
import { EXCHANGE_GATEWAYS_ROUTE } from './exchange-gateways.route';
import { ExchangeGatewaysComponent } from './exchange-gateways.component';
import { AddExchangeGatewayComponent } from './add-exchange-gateway.component';

@NgModule({
  imports: [TabotSharedModule, RouterModule.forChild([EXCHANGE_GATEWAYS_ROUTE])],
  declarations: [ExchangeGatewaysComponent, AddExchangeGatewayComponent],
})
export class ExchangeGatewaysModule {}
