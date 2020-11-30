import { Route } from '@angular/router';

import { DashboardComponent } from '../application/component/dashboard/dashboard.component';

export const HOME_ROUTE: Route = {
  path: '',
  component: DashboardComponent,
  data: {
    authorities: [],
    pageTitle: 'dashboard.title',
  },
};
