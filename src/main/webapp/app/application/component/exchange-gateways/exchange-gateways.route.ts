import { Route } from '@angular/router';
import { ExchangeGatewaysComponent } from './exchange-gateways.component';

export const EXCHANGE_GATEWAYS_ROUTE: Route = {
  path: '',
  component: ExchangeGatewaysComponent,
  data: {
    authorities: [],
    pageTitle: 'exchange-gateways.title',
  },
};
