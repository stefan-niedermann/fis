import {TestBed} from '@angular/core/testing';

import {WebSocketService} from './web-socket.service';
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('WebSocketService', () => {
  let service: WebSocketService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
    });
    service = TestBed.inject(WebSocketService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
