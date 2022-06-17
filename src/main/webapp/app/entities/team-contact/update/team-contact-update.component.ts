import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ITeamContact, TeamContact } from '../team-contact.model';
import { TeamContactService } from '../service/team-contact.service';
import { IContact } from 'app/entities/contact/contact.model';
import { ContactService } from 'app/entities/contact/service/contact.service';
import { ITeam } from 'app/entities/team/team.model';
import { TeamService } from 'app/entities/team/service/team.service';
import { RoleType } from 'app/entities/enumerations/role-type.model';

@Component({
  selector: 'jhi-team-contact-update',
  templateUrl: './team-contact-update.component.html',
})
export class TeamContactUpdateComponent implements OnInit {
  isSaving = false;
  roleTypeValues = Object.keys(RoleType);

  contactsSharedCollection: IContact[] = [];
  teamsSharedCollection: ITeam[] = [];

  editForm = this.fb.group({
    id: [],
    roleType: [],
    role: [null, [Validators.required]],
    description: [],
    contact: [],
    team: [],
  });

  constructor(
    protected teamContactService: TeamContactService,
    protected contactService: ContactService,
    protected teamService: TeamService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teamContact }) => {
      this.updateForm(teamContact);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const teamContact = this.createFromForm();
    if (teamContact.id !== undefined) {
      this.subscribeToSaveResponse(this.teamContactService.update(teamContact));
    } else {
      this.subscribeToSaveResponse(this.teamContactService.create(teamContact));
    }
  }

  trackContactById(_index: number, item: IContact): number {
    return item.id!;
  }

  trackTeamById(_index: number, item: ITeam): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeamContact>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(teamContact: ITeamContact): void {
    this.editForm.patchValue({
      id: teamContact.id,
      roleType: teamContact.roleType,
      role: teamContact.role,
      description: teamContact.description,
      contact: teamContact.contact,
      team: teamContact.team,
    });

    this.contactsSharedCollection = this.contactService.addContactToCollectionIfMissing(this.contactsSharedCollection, teamContact.contact);
    this.teamsSharedCollection = this.teamService.addTeamToCollectionIfMissing(this.teamsSharedCollection, teamContact.team);
  }

  protected loadRelationshipsOptions(): void {
    this.contactService
      .query()
      .pipe(map((res: HttpResponse<IContact[]>) => res.body ?? []))
      .pipe(
        map((contacts: IContact[]) => this.contactService.addContactToCollectionIfMissing(contacts, this.editForm.get('contact')!.value))
      )
      .subscribe((contacts: IContact[]) => (this.contactsSharedCollection = contacts));

    this.teamService
      .query()
      .pipe(map((res: HttpResponse<ITeam[]>) => res.body ?? []))
      .pipe(map((teams: ITeam[]) => this.teamService.addTeamToCollectionIfMissing(teams, this.editForm.get('team')!.value)))
      .subscribe((teams: ITeam[]) => (this.teamsSharedCollection = teams));
  }

  protected createFromForm(): ITeamContact {
    return {
      ...new TeamContact(),
      id: this.editForm.get(['id'])!.value,
      roleType: this.editForm.get(['roleType'])!.value,
      role: this.editForm.get(['role'])!.value,
      description: this.editForm.get(['description'])!.value,
      contact: this.editForm.get(['contact'])!.value,
      team: this.editForm.get(['team'])!.value,
    };
  }
}
