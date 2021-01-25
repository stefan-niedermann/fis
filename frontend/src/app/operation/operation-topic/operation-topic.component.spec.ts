import {ComponentFixture, TestBed} from '@angular/core/testing';

import {OperationTopicComponent} from './operation-topic.component';
import {OperationKeywordPipe} from "./operation-keyword.pipe";

describe('OperationTopicComponent', () => {
  let component: OperationTopicComponent;
  let fixture: ComponentFixture<OperationTopicComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        OperationTopicComponent,
        OperationKeywordPipe
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OperationTopicComponent);
    component = fixture.componentInstance;
    (component as any).operation = {};
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
