import {Injectable} from '@angular/core'
import {HttpClient} from '@angular/common/http'
import {Observable, of} from 'rxjs'
import {Parameter} from './domain/parameter'
import {environment} from '../environments/environment'
import {share, take, tap} from 'rxjs/operators'

@Injectable({
  providedIn: 'root'
})
export class ParameterService {

  private parameter!: Parameter

  constructor(
    private http: HttpClient
  ) {
  }

  public getParameter(): Observable<Parameter> {
    if (this.parameter === undefined) {
      return this.http.get<Parameter>(`${environment.hostUrl}/parameter`).pipe(take(1))
        .pipe(
          tap(parameter => console.info('⚙️ New parameter (polled):', parameter)),
          tap(parameter => this.parameter = parameter),
          share()
        )
    }
    return of(this.parameter)
  }
}
