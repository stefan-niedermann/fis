import {TestBed} from '@angular/core/testing'
import {ParameterService} from './parameter.service'
import {DefaultService, Parameter} from "./gen";
import {of, take} from "rxjs";
import {HttpHeaders, HttpResponse} from "@angular/common/http";

describe('ParameterService', () => {
  let service: ParameterService
  let getParameterMock = jest.fn(() => {
    return of(new HttpResponse({
      status: 200,
      headers: new HttpHeaders('ETag: XYZ'),
      body: {
        highlight: 'foo'
      } as Parameter
    }))
  })

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: DefaultService, useValue: {
            getParameter: getParameterMock
          }
        }
      ]
    })
    service = TestBed.inject(ParameterService)
  })

  it('should be created', () => {
    expect(service).toBeTruthy()
  })

  it('should fetch parameters on startup', (done) => {
    service.getParameter().pipe(take(1)).subscribe(() => {
      expect(getParameterMock).toHaveBeenCalled()
      done()
    })
  })

  it('should send received ETags as If-None-Match header with the second request', (done) => {
    let firstRequest = true
    service.getParameter().pipe(take(2)).subscribe(() => {
      if (firstRequest) {
        expect(getParameterMock).toHaveBeenCalledWith(undefined, 'response')
        firstRequest = false
      } else {
        expect(getParameterMock).toHaveBeenLastCalledWith('XYZ', 'response')
        done()
      }
    })
  })
})

