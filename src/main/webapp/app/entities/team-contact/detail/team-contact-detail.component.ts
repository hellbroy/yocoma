import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITeamContact } from '../team-contact.model';

@Component({
  selector: 'jhi-team-contact-detail',
  templateUrl: './team-contact-detail.component.html',
})
export class TeamContactDetailComponent implements OnInit {
  teamContact: ITeamContact | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teamContact }) => {
      this.teamContact = teamContact;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
