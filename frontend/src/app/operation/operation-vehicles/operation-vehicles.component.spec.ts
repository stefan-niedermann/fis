import {ComponentFixture, TestBed} from '@angular/core/testing';

import {OperationVehiclesComponent} from './operation-vehicles.component';

describe('OperationVehiclesComponent', () => {
  let component: OperationVehiclesComponent;
  let fixture: ComponentFixture<OperationVehiclesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OperationVehiclesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OperationVehiclesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
