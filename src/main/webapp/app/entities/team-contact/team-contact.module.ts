import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TeamContactComponent } from './list/team-contact.component';
import { TeamContactDetailComponent } from './detail/team-contact-detail.component';
import { TeamContactUpdateComponent } from './update/team-contact-update.component';
import { TeamContactDeleteDialogComponent } from './delete/team-contact-delete-dialog.component';
import { TeamContactRoutingModule } from './route/team-contact-routing.module';

@NgModule({
  imports: [SharedModule, TeamContactRoutingModule],
  declarations: [TeamContactComponent, TeamContactDetailComponent, TeamContactUpdateComponent, TeamContactDeleteDialogComponent],
  entryComponents: [TeamContactDeleteDialogComponent],
})
export class TeamContactModule {}
