import {TestBed} from '@angular/core/testing';

import {InfoService} from './info.service';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {MockProvider} from 'ng-mocks';
import {WebSocketService} from '../web-socket.service';
import {EMPTY} from 'rxjs';

describe('InfoService', () => {
  let service: InfoService

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ],
      providers: [
        MockProvider(WebSocketService, {
          subscribe: () => EMPTY
        })
      ]
    })
    service = TestBed.inject(InfoService)
  })

  it('should be created', () => {
    expect(service).toBeTruthy()
  })
})
