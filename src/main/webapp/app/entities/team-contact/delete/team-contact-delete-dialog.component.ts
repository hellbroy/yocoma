import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITeamContact } from '../team-contact.model';
import { TeamContactService } from '../service/team-contact.service';

@Component({
  templateUrl: './team-contact-delete-dialog.component.html',
})
export class TeamContactDeleteDialogComponent {
  teamContact?: ITeamContact;

  constructor(protected teamContactService: TeamContactService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.teamContactService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
