import {TestBed} from '@angular/core/testing';

import {InfoService, POLL_INTERVAL_WEATHER} from './info.service';
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('InfoService', () => {
  let service: InfoService

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ],
      providers: [
        {provide: POLL_INTERVAL_WEATHER, useValue: 1}
      ]
    })
    service = TestBed.inject(InfoService)
  })

  it('should be created', () => {
    expect(service).toBeTruthy()
  })
})
