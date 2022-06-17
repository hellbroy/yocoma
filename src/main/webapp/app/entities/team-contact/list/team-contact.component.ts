import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITeamContact } from '../team-contact.model';
import { TeamContactService } from '../service/team-contact.service';
import { TeamContactDeleteDialogComponent } from '../delete/team-contact-delete-dialog.component';

@Component({
  selector: 'jhi-team-contact',
  templateUrl: './team-contact.component.html',
})
export class TeamContactComponent implements OnInit {
  teamContacts?: ITeamContact[];
  isLoading = false;

  constructor(protected teamContactService: TeamContactService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.teamContactService.query().subscribe({
      next: (res: HttpResponse<ITeamContact[]>) => {
        this.isLoading = false;
        this.teamContacts = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ITeamContact): number {
    return item.id!;
  }

  delete(teamContact: ITeamContact): void {
    const modalRef = this.modalService.open(TeamContactDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.teamContact = teamContact;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
