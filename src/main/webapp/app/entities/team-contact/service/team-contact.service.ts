import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITeamContact, getTeamContactIdentifier } from '../team-contact.model';

export type EntityResponseType = HttpResponse<ITeamContact>;
export type EntityArrayResponseType = HttpResponse<ITeamContact[]>;

@Injectable({ providedIn: 'root' })
export class TeamContactService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/team-contacts');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(teamContact: ITeamContact): Observable<EntityResponseType> {
    return this.http.post<ITeamContact>(this.resourceUrl, teamContact, { observe: 'response' });
  }

  update(teamContact: ITeamContact): Observable<EntityResponseType> {
    return this.http.put<ITeamContact>(`${this.resourceUrl}/${getTeamContactIdentifier(teamContact) as number}`, teamContact, {
      observe: 'response',
    });
  }

  partialUpdate(teamContact: ITeamContact): Observable<EntityResponseType> {
    return this.http.patch<ITeamContact>(`${this.resourceUrl}/${getTeamContactIdentifier(teamContact) as number}`, teamContact, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITeamContact>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITeamContact[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTeamContactToCollectionIfMissing(
    teamContactCollection: ITeamContact[],
    ...teamContactsToCheck: (ITeamContact | null | undefined)[]
  ): ITeamContact[] {
    const teamContacts: ITeamContact[] = teamContactsToCheck.filter(isPresent);
    if (teamContacts.length > 0) {
      const teamContactCollectionIdentifiers = teamContactCollection.map(teamContactItem => getTeamContactIdentifier(teamContactItem)!);
      const teamContactsToAdd = teamContacts.filter(teamContactItem => {
        const teamContactIdentifier = getTeamContactIdentifier(teamContactItem);
        if (teamContactIdentifier == null || teamContactCollectionIdentifiers.includes(teamContactIdentifier)) {
          return false;
        }
        teamContactCollectionIdentifiers.push(teamContactIdentifier);
        return true;
      });
      return [...teamContactsToAdd, ...teamContactCollection];
    }
    return teamContactCollection;
  }
}
