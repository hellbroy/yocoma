import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TeamContactDetailComponent } from './team-contact-detail.component';

describe('TeamContact Management Detail Component', () => {
  let comp: TeamContactDetailComponent;
  let fixture: ComponentFixture<TeamContactDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TeamContactDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ teamContact: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TeamContactDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TeamContactDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load teamContact on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.teamContact).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
