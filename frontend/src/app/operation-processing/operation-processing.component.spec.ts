import {ComponentFixture, TestBed} from '@angular/core/testing';

import {OperationProcessingComponent} from './operation-processing.component';

describe('OperationProcessingComponent', () => {
  let component: OperationProcessingComponent;
  let fixture: ComponentFixture<OperationProcessingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OperationProcessingComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OperationProcessingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
