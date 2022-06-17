import { IContact } from 'app/entities/contact/contact.model';
import { ITeam } from 'app/entities/team/team.model';
import { RoleType } from 'app/entities/enumerations/role-type.model';

export interface ITeamContact {
  id?: number;
  roleType?: RoleType | null;
  role?: string;
  description?: string | null;
  contact?: IContact | null;
  team?: ITeam | null;
}

export class TeamContact implements ITeamContact {
  constructor(
    public id?: number,
    public roleType?: RoleType | null,
    public role?: string,
    public description?: string | null,
    public contact?: IContact | null,
    public team?: ITeam | null
  ) {}
}

export function getTeamContactIdentifier(teamContact: ITeamContact): number | undefined {
  return teamContact.id;
}
