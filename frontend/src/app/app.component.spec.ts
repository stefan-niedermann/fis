import {TestBed} from '@angular/core/testing';
import {AppComponent} from './app.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import { MockComponent, MockProvider } from 'ng-mocks';
import { Renderer2 } from '@angular/core';
import { InfoService } from './info/info.service';
import { InfoComponent } from './info/info.component';
import { OperationComponent } from './operation/operation.component';
import { EMPTY } from 'rxjs';
import { DOCUMENT } from '@angular/common';
import { OperationService } from './operation/operation.service';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      declarations: [
        AppComponent,
        MockComponent(InfoComponent),
        MockComponent(OperationComponent)
      ],
      providers: [
        MockProvider(Document),
        MockProvider(Renderer2),
        MockProvider(InfoService, {
          isDarkTheme: () => EMPTY
        }),
        MockProvider(OperationService)
      ]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

});
