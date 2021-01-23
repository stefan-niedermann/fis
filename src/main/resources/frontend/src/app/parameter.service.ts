import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable, of} from "rxjs";
import {Parameter} from "./domain/parameter";
import {environment} from "../environments/environment";
import {take, tap} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class ParameterService {

  private parameter: Parameter;

  constructor(
    private http: HttpClient
  ) {
  }

  public getParameter(): Observable<Parameter> {
    if (!this.parameter) {
      return this.http.get<Parameter>(`${environment.hostUrl}/parameter`).pipe(take(1))
        .pipe(tap(parameter => this.parameter = parameter));
    }
    return of(this.parameter);
  }
}
