import {OperationService} from './operation.service'

import {of, take} from "rxjs";
import {HttpHeaders, HttpResponse} from "@angular/common/http";
import {ParameterService} from "../parameter.service";

describe('OperationService', () => {
  let service: OperationService
  let getOperation = jest.fn((ifNoneMatch) => {
    return ifNoneMatch === 'XYZ'
      ? of(new HttpResponse({
        status: 304,
        headers: new HttpHeaders('ETag: XYZ')
      }))
      : of(new HttpResponse({
        status: 200,
        headers: new HttpHeaders('ETag: XYZ'),
        body: {
          keyword: 'B 1',
          number: '5',
          street: 'samplestreet',
          location: 'Samplecity',
          obj: '',
          tags: '',
          vehicles: '',
          note: ''
        }
      }))
  })

  beforeEach(() => service = new OperationService({getParameter: () => of(1)} as ParameterService, {getOperation} as any))

  it('should be created', () => expect(service).toBeTruthy())

  it('should fetch operations on startup', (done) => {
    service.getActiveOperation().pipe(take(1)).subscribe(() => {
      expect(getOperation).toHaveBeenCalled()
      done()
    })
  })

  xit('should send received ETags as If-None-Match header with the second request', (done) => {
    let firstRequest = true
    service.getActiveOperation().pipe(take(2)).subscribe(() => {
      if (firstRequest) {
        expect(getOperation).toHaveBeenCalledWith(undefined, 'response')
        firstRequest = false
      } else {
        expect(getOperation).toHaveBeenLastCalledWith('XYZ', 'response')
        done()
      }
    })
  })
})
