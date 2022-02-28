import {InfoService} from "./info.service";
import {EMPTY, of} from "rxjs";
import {ParameterService} from "../parameter.service";

describe('InfoService', () => {
  let service: InfoService

  beforeEach(() => {
    service = new InfoService({getParameter: () => of(1)} as ParameterService, {getWeather: () => EMPTY} as any)
  })

  it('should be created', () => {
    expect(service).toBeTruthy()
  })
})
