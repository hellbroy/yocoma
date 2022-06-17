import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { RoleType } from 'app/entities/enumerations/role-type.model';
import { ITeamContact, TeamContact } from '../team-contact.model';

import { TeamContactService } from './team-contact.service';

describe('TeamContact Service', () => {
  let service: TeamContactService;
  let httpMock: HttpTestingController;
  let elemDefault: ITeamContact;
  let expectedResult: ITeamContact | ITeamContact[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TeamContactService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      roleType: RoleType.JUNIOR_MEMBER,
      role: 'AAAAAAA',
      description: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a TeamContact', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new TeamContact()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TeamContact', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          roleType: 'BBBBBB',
          role: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TeamContact', () => {
      const patchObject = Object.assign(
        {
          roleType: 'BBBBBB',
          role: 'BBBBBB',
        },
        new TeamContact()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TeamContact', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          roleType: 'BBBBBB',
          role: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a TeamContact', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTeamContactToCollectionIfMissing', () => {
      it('should add a TeamContact to an empty array', () => {
        const teamContact: ITeamContact = { id: 123 };
        expectedResult = service.addTeamContactToCollectionIfMissing([], teamContact);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(teamContact);
      });

      it('should not add a TeamContact to an array that contains it', () => {
        const teamContact: ITeamContact = { id: 123 };
        const teamContactCollection: ITeamContact[] = [
          {
            ...teamContact,
          },
          { id: 456 },
        ];
        expectedResult = service.addTeamContactToCollectionIfMissing(teamContactCollection, teamContact);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TeamContact to an array that doesn't contain it", () => {
        const teamContact: ITeamContact = { id: 123 };
        const teamContactCollection: ITeamContact[] = [{ id: 456 }];
        expectedResult = service.addTeamContactToCollectionIfMissing(teamContactCollection, teamContact);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(teamContact);
      });

      it('should add only unique TeamContact to an array', () => {
        const teamContactArray: ITeamContact[] = [{ id: 123 }, { id: 456 }, { id: 57148 }];
        const teamContactCollection: ITeamContact[] = [{ id: 123 }];
        expectedResult = service.addTeamContactToCollectionIfMissing(teamContactCollection, ...teamContactArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const teamContact: ITeamContact = { id: 123 };
        const teamContact2: ITeamContact = { id: 456 };
        expectedResult = service.addTeamContactToCollectionIfMissing([], teamContact, teamContact2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(teamContact);
        expect(expectedResult).toContain(teamContact2);
      });

      it('should accept null and undefined values', () => {
        const teamContact: ITeamContact = { id: 123 };
        expectedResult = service.addTeamContactToCollectionIfMissing([], null, teamContact, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(teamContact);
      });

      it('should return initial array if no TeamContact is added', () => {
        const teamContactCollection: ITeamContact[] = [{ id: 123 }];
        expectedResult = service.addTeamContactToCollectionIfMissing(teamContactCollection, undefined, null);
        expect(expectedResult).toEqual(teamContactCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
