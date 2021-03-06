import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { errorRoute } from './layouts/error/error.route';
import { navbarRoute } from './layouts/navbar/navbar.route';
import { DEBUG_INFO_ENABLED } from 'app/app.constants';
import { Authority } from 'app/shared/constants/authority.constants';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';

const LAYOUT_ROUTES = [navbarRoute, ...errorRoute];

@NgModule({
  imports: [
    RouterModule.forRoot(
      [
        {
          path: 'admin',
          data: {
            authorities: [Authority.ADMIN],
          },
          canActivate: [UserRouteAccessService],
          loadChildren: () => import('./admin/admin-routing.module').then(m => m.AdminRoutingModule),
        },
        {
          path: 'account',
          loadChildren: () => import('./account/account.module').then(m => m.AccountModule),
        },
        {
          path: 'dashboard',
          loadChildren: () => import('./application/component/dashboard/dashboard.module').then(m => m.DashboardModule),
        },
        {
          path: 'exchange-gateways',
          loadChildren: () =>
            import('./application/component/exchange-gateways/exchange-gateways.module').then(m => m.ExchangeGatewaysModule),
        },
        {
          path: 'strategies',
          loadChildren: () => import('./application/component/strategies/strategies.module').then(m => m.StrategiesModule),
        },
        {
          path: 'strategy-executions',
          loadChildren: () =>
            import('./application/component/strategy-executions/strategy-executions.module').then(m => m.StrategyExecutionsModule),
        },
        {
          path: 'history',
          loadChildren: () => import('./application/component/history/history.module').then(m => m.HistoryModule),
        },
        {
          path: 'sign-in',
          loadChildren: () => import('./shared/sign-in/sign-in.module').then(m => m.SignInModule),
        },
        ...LAYOUT_ROUTES,
      ],
      { enableTracing: DEBUG_INFO_ENABLED }
    ),
  ],
  exports: [RouterModule],
})
export class TabotAppRoutingModule {}
