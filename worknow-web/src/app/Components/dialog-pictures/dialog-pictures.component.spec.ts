import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogPicturesComponent } from './dialog-pictures.component';

describe('DialogPicturesComponent', () => {
  let component: DialogPicturesComponent;
  let fixture: ComponentFixture<DialogPicturesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogPicturesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogPicturesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
