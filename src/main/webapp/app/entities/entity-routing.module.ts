import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'team-contact',
        data: { pageTitle: 'yocomaApp.teamContact.home.title' },
        loadChildren: () => import('./team-contact/team-contact.module').then(m => m.TeamContactModule),
      },
      {
        path: 'team',
        data: { pageTitle: 'yocomaApp.team.home.title' },
        loadChildren: () => import('./team/team.module').then(m => m.TeamModule),
      },
      {
        path: 'contact',
        data: { pageTitle: 'yocomaApp.contact.home.title' },
        loadChildren: () => import('./contact/contact.module').then(m => m.ContactModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
