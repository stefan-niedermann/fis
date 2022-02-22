import {Injectable} from '@angular/core'
import {HttpClient} from '@angular/common/http'
import {Observable, of} from 'rxjs'
import {share, tap} from 'rxjs/operators'
import {DefaultService, Parameter} from "./gen";

@Injectable({
  providedIn: 'root'
})
export class ParameterService {

  private parameter!: Parameter

  constructor(
    private http: HttpClient,
    private apiService: DefaultService
  ) {
  }

  public getParameter(): Observable<Parameter> {
    if (this.parameter === undefined) {
      return this.apiService.parameterGet()
        .pipe(
          tap(parameter => console.info('⚙️ New parameter (polled):', parameter)),
          tap(parameter => this.parameter = parameter),
          share()
        )
    }
    return of(this.parameter)
  }
}
