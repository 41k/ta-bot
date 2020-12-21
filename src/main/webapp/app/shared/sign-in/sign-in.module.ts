import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { TabotSharedModule } from 'app/shared/shared.module';
import { SIGN_IN_ROUTE } from './sign-in.route';
import { SignInComponent } from './sign-in.component';

@NgModule({
  imports: [TabotSharedModule, RouterModule.forChild([SIGN_IN_ROUTE])],
  declarations: [SignInComponent],
})
export class SignInModule {}
