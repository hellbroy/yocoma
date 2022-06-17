import { ITeamContact } from 'app/entities/team-contact/team-contact.model';

export interface IContact {
  id?: number;
  firstname?: string;
  lastname?: string;
  email?: string | null;
  phone?: string | null;
  remark?: string | null;
  imageContentType?: string | null;
  image?: string | null;
  teamContacts?: ITeamContact[] | null;
}

export class Contact implements IContact {
  constructor(
    public id?: number,
    public firstname?: string,
    public lastname?: string,
    public email?: string | null,
    public phone?: string | null,
    public remark?: string | null,
    public imageContentType?: string | null,
    public image?: string | null,
    public teamContacts?: ITeamContact[] | null
  ) {}
}

export function getContactIdentifier(contact: IContact): number | undefined {
  return contact.id;
}
