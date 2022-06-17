import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TeamContactService } from '../service/team-contact.service';
import { ITeamContact, TeamContact } from '../team-contact.model';
import { IContact } from 'app/entities/contact/contact.model';
import { ContactService } from 'app/entities/contact/service/contact.service';
import { ITeam } from 'app/entities/team/team.model';
import { TeamService } from 'app/entities/team/service/team.service';

import { TeamContactUpdateComponent } from './team-contact-update.component';

describe('TeamContact Management Update Component', () => {
  let comp: TeamContactUpdateComponent;
  let fixture: ComponentFixture<TeamContactUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let teamContactService: TeamContactService;
  let contactService: ContactService;
  let teamService: TeamService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TeamContactUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(TeamContactUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TeamContactUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    teamContactService = TestBed.inject(TeamContactService);
    contactService = TestBed.inject(ContactService);
    teamService = TestBed.inject(TeamService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Contact query and add missing value', () => {
      const teamContact: ITeamContact = { id: 456 };
      const contact: IContact = { id: 44540 };
      teamContact.contact = contact;

      const contactCollection: IContact[] = [{ id: 32986 }];
      jest.spyOn(contactService, 'query').mockReturnValue(of(new HttpResponse({ body: contactCollection })));
      const additionalContacts = [contact];
      const expectedCollection: IContact[] = [...additionalContacts, ...contactCollection];
      jest.spyOn(contactService, 'addContactToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teamContact });
      comp.ngOnInit();

      expect(contactService.query).toHaveBeenCalled();
      expect(contactService.addContactToCollectionIfMissing).toHaveBeenCalledWith(contactCollection, ...additionalContacts);
      expect(comp.contactsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Team query and add missing value', () => {
      const teamContact: ITeamContact = { id: 456 };
      const team: ITeam = { id: 34876 };
      teamContact.team = team;

      const teamCollection: ITeam[] = [{ id: 71068 }];
      jest.spyOn(teamService, 'query').mockReturnValue(of(new HttpResponse({ body: teamCollection })));
      const additionalTeams = [team];
      const expectedCollection: ITeam[] = [...additionalTeams, ...teamCollection];
      jest.spyOn(teamService, 'addTeamToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teamContact });
      comp.ngOnInit();

      expect(teamService.query).toHaveBeenCalled();
      expect(teamService.addTeamToCollectionIfMissing).toHaveBeenCalledWith(teamCollection, ...additionalTeams);
      expect(comp.teamsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const teamContact: ITeamContact = { id: 456 };
      const contact: IContact = { id: 49256 };
      teamContact.contact = contact;
      const team: ITeam = { id: 2486 };
      teamContact.team = team;

      activatedRoute.data = of({ teamContact });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(teamContact));
      expect(comp.contactsSharedCollection).toContain(contact);
      expect(comp.teamsSharedCollection).toContain(team);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TeamContact>>();
      const teamContact = { id: 123 };
      jest.spyOn(teamContactService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamContact });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teamContact }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(teamContactService.update).toHaveBeenCalledWith(teamContact);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TeamContact>>();
      const teamContact = new TeamContact();
      jest.spyOn(teamContactService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamContact });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teamContact }));
      saveSubject.complete();

      // THEN
      expect(teamContactService.create).toHaveBeenCalledWith(teamContact);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TeamContact>>();
      const teamContact = { id: 123 };
      jest.spyOn(teamContactService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamContact });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(teamContactService.update).toHaveBeenCalledWith(teamContact);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackContactById', () => {
      it('Should return tracked Contact primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackContactById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackTeamById', () => {
      it('Should return tracked Team primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTeamById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
