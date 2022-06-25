import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { Observable, Subject } from 'rxjs';

import { ITeam } from '../team.model';
import { TeamService } from '../service/team.service';
import { TeamDeleteDialogComponent } from '../delete/team-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-team',
  templateUrl: './team.component.html',
})
export class TeamComponent implements OnInit {
  teams?: ITeam[];
  searchprompt: string;
  isLoading = false;

  constructor(protected teamService: TeamService, protected dataUtils: DataUtils, protected modalService: NgbModal) {
    this.searchprompt = "";
  }

  setSearchprompt(searchprompt: string): void {
    this.searchprompt = searchprompt;
  }

  loadAll(): void {
    this.isLoading = true;

    this.teamService.query({'search.contains': this.searchprompt}).subscribe({
      next: (res: HttpResponse<ITeam[]>) => {
        this.isLoading = false;
        this.teams = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ITeam): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(team: ITeam): void {
    const modalRef = this.modalService.open(TeamDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.team = team;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
