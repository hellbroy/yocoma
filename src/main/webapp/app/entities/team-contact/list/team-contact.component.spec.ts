import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { TeamContactService } from '../service/team-contact.service';

import { TeamContactComponent } from './team-contact.component';

describe('TeamContact Management Component', () => {
  let comp: TeamContactComponent;
  let fixture: ComponentFixture<TeamContactComponent>;
  let service: TeamContactService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TeamContactComponent],
    })
      .overrideTemplate(TeamContactComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TeamContactComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TeamContactService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.teamContacts?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
