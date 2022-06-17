import { ITeamContact } from 'app/entities/team-contact/team-contact.model';

export interface ITeam {
  id?: number;
  name?: string;
  motto?: string | null;
  logoContentType?: string | null;
  logo?: string | null;
  description?: string | null;
  teamContacts?: ITeamContact[] | null;
}

export class Team implements ITeam {
  constructor(
    public id?: number,
    public name?: string,
    public motto?: string | null,
    public logoContentType?: string | null,
    public logo?: string | null,
    public description?: string | null,
    public teamContacts?: ITeamContact[] | null
  ) {}
}

export function getTeamIdentifier(team: ITeam): number | undefined {
  return team.id;
}
