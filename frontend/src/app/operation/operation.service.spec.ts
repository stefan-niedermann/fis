import {TestBed} from '@angular/core/testing';

import {OperationService} from './operation.service';
import {HttpClientTestingModule, HttpTestingController, TestRequest} from '@angular/common/http/testing';

import {MockProvider} from 'ng-mocks'
import {WebSocketService} from '../web-socket.service';
import {EMPTY} from 'rxjs';
import {environment} from '../../environments/environment';

describe('OperationService', () => {
  let service: OperationService;
  let httpMock: HttpTestingController;
  let firstRequest: TestRequest;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        MockProvider(WebSocketService, {
          subscribe: () => EMPTY
        })
      ]
    });
    service = TestBed.inject(OperationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  beforeEach(() => {
    firstRequest = httpMock.expectOne({
      url: `${environment.hostUrl}/operation`,
      method: 'get'
    });
  })

  afterEach(() => {
    httpMock.verify();
  })

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should expose the active operation state', (done) => {
    firstRequest.flush({
      keyword: 'B 1',
      number: '5',
      street: 'samplestreet',
      location: 'Samplecity',
      obj: '',
      tags: '',
      vehicles: '',
      note: ''
    });

    service
      .isActiveOperation()
      .subscribe({
        next: (active) => {
          expect(active).toBeTruthy();
          done();
        },
        error: () => fail(),
        complete: () => fail()
      });
  })

  it('should expose any active operation', (done) => {
    firstRequest.flush({
      keyword: 'B 1',
      number: '5',
      street: 'samplestreet',
      location: 'Samplecity',
      obj: '',
      tags: '',
      vehicles: '',
      note: ''
    });

    service
      .getActiveOperation()
      .subscribe({
        next: (operation) => {
          expect(operation.keyword).toEqual('B 1');
          expect(operation.number).toEqual('5');
          expect(operation.street).toEqual('samplestreet');
          expect(operation.location).toEqual('Samplecity');
          done();
        },
        error: () => fail(),
        complete: () => fail()
      });
  })
});
