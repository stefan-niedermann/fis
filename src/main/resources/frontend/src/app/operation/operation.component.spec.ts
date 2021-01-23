import {ComponentFixture, TestBed} from '@angular/core/testing';

import {OperationComponent} from './operation.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {OperationLocationPipe} from "./operation-location.pipe";

describe('OperationComponent', () => {
  let component: OperationComponent;
  let fixture: ComponentFixture<OperationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      declarations: [
        OperationComponent,
        OperationLocationPipe
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
