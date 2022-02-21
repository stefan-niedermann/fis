import {ComponentFixture, TestBed} from '@angular/core/testing';

import {InfoComponent} from './info.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {IconUrlPipe} from "./icon-url.pipe";
import {ClockPipe} from "./clock.pipe";
import {TemperaturePipe} from "./temperature.pipe";
import {MockProvider} from 'ng-mocks';
import {InfoService} from './info.service';
import {EMPTY} from 'rxjs';

describe('InfoComponent', () => {
  let component: InfoComponent
  let fixture: ComponentFixture<InfoComponent>

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        MockProvider(InfoService, {
          getCurrentWeather: () => EMPTY,
          getCurrentTime: () => EMPTY
        })
      ],
      declarations: [
        InfoComponent,
        IconUrlPipe,
        ClockPipe,
        TemperaturePipe
      ]
    })
    .compileComponents()
  })

  beforeEach(() => {
    fixture = TestBed.createComponent(InfoComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })
})
