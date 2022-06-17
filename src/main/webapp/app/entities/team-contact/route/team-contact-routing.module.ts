import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TeamContactComponent } from '../list/team-contact.component';
import { TeamContactDetailComponent } from '../detail/team-contact-detail.component';
import { TeamContactUpdateComponent } from '../update/team-contact-update.component';
import { TeamContactRoutingResolveService } from './team-contact-routing-resolve.service';

const teamContactRoute: Routes = [
  {
    path: '',
    component: TeamContactComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TeamContactDetailComponent,
    resolve: {
      teamContact: TeamContactRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TeamContactUpdateComponent,
    resolve: {
      teamContact: TeamContactRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TeamContactUpdateComponent,
    resolve: {
      teamContact: TeamContactRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(teamContactRoute)],
  exports: [RouterModule],
})
export class TeamContactRoutingModule {}
