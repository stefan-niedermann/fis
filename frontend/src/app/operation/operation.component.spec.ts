import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OperationComponent } from './operation.component';
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { MockComponent, MockProvider } from 'ng-mocks';
import { OperationVehiclesComponent } from './operation-vehicles/operation-vehicles.component';
import { OperationTopicComponent } from './operation-topic/operation-topic.component';
import { OperationLocationComponent } from './operation-location/operation-location.component';
import { OperationService } from './operation.service';
import { ParameterService } from '../parameter.service';
import { EMPTY } from 'rxjs';

describe('OperationComponent', () => {
  let component: OperationComponent;
  let fixture: ComponentFixture<OperationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        MockProvider(OperationService, {
          getActiveOperation: () => EMPTY
        }),
        MockProvider(ParameterService, {
          getParameter: () => EMPTY
        })
      ],
      declarations: [
        OperationComponent,
        MockComponent(OperationVehiclesComponent),
        MockComponent(OperationTopicComponent),
        MockComponent(OperationLocationComponent)
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OperationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
