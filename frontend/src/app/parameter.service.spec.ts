import {ParameterService} from './parameter.service'
import {of, take} from "rxjs";
import {HttpHeaders, HttpResponse} from "@angular/common/http";

describe('ParameterService', () => {
  let service: ParameterService
  let getParameter = jest.fn((ifNoneMatch) => {
    return ifNoneMatch === 'XYZ'
      ? of(new HttpResponse({
        status: 304,
        headers: new HttpHeaders('ETag: XYZ')
      }))
      : of(new HttpResponse({
        status: 200,
        headers: new HttpHeaders('ETag: XYZ'),
        body: {highlight: 'foo'}
      }))
  })

  beforeEach(() => service = new ParameterService(1, {getParameter} as any))

  it('should be created', () => expect(service).toBeTruthy())

  it('should fetch parameters on startup', (done) => {
    service.getParameter().pipe(take(1)).subscribe(() => {
      expect(getParameter).toHaveBeenCalled()
      done()
    })
  })

  it('should send received ETags as If-None-Match header with the second request', (done) => {
    let firstRequest = true
    service.getParameter().pipe(take(2)).subscribe(() => {
      if (firstRequest) {
        expect(getParameter).toHaveBeenCalledWith(undefined, 'response')
        firstRequest = false
      } else {
        expect(getParameter).toHaveBeenLastCalledWith('XYZ', 'response')
        done()
      }
    })
  })
})

