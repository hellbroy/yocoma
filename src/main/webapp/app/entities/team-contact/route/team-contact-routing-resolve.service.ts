import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITeamContact, TeamContact } from '../team-contact.model';
import { TeamContactService } from '../service/team-contact.service';

@Injectable({ providedIn: 'root' })
export class TeamContactRoutingResolveService implements Resolve<ITeamContact> {
  constructor(protected service: TeamContactService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITeamContact> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((teamContact: HttpResponse<TeamContact>) => {
          if (teamContact.body) {
            return of(teamContact.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new TeamContact());
  }
}
