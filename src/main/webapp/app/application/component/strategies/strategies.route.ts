import { Route } from '@angular/router';
import { StrategiesComponent } from './strategies.component';

export const STRATEGIES_ROUTE: Route = {
  path: '',
  component: StrategiesComponent,
  data: {
    authorities: [],
    pageTitle: 'strategies.title',
  },
};
