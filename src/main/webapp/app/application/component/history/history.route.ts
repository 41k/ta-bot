import { Route } from '@angular/router';

import { HistoryComponent } from './history.component';

export const HISTORY_ROUTE: Route = {
  path: '',
  component: HistoryComponent,
  data: {
    authorities: [],
    pageTitle: 'history.title',
  },
};
