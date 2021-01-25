import {ComponentFixture, TestBed} from '@angular/core/testing';

import {OperationLocationComponent} from './operation-location.component';
import {OperationLocationPipe} from "../operation-location.pipe";

describe('OperationLocationComponent', () => {
  let component: OperationLocationComponent;
  let fixture: ComponentFixture<OperationLocationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        OperationLocationComponent,
        OperationLocationPipe
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OperationLocationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
