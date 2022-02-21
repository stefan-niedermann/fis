import {TestBed} from '@angular/core/testing'
import {ParameterService} from './parameter.service'
import {HttpClientTestingModule} from '@angular/common/http/testing'

describe('ParameterService', () => {
  let service: ParameterService

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
    })
    service = TestBed.inject(ParameterService)
  })

  it('should be created', () => {
    expect(service).toBeTruthy()
  })
})
