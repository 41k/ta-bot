import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { TabotSharedModule } from 'app/shared/shared.module';
import { TabotCoreModule } from 'app/core/core.module';
import { TabotAppRoutingModule } from './app-routing.module';
import { TabotHomeModule } from './home/home.module';
import { TabotEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { ActiveMenuDirective } from './layouts/navbar/active-menu.directive';
import { ErrorComponent } from './layouts/error/error.component';

@NgModule({
  imports: [
    BrowserModule,
    TabotSharedModule,
    TabotCoreModule,
    TabotHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    TabotEntityModule,
    TabotAppRoutingModule,
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, ActiveMenuDirective],
  bootstrap: [MainComponent],
})
export class TabotAppModule {}
