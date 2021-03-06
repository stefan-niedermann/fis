import {Injectable} from '@angular/core';
import {BehaviorSubject, merge, Observable, Subject} from "rxjs";
import {WebSocketService} from "../web-socket.service";
import {map, tap} from "rxjs/operators";
import {Operation} from "../domain/operation";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class OperationService {

  private readonly activeOperation$: Subject<Operation> = new BehaviorSubject<Operation>(null);

  constructor(
    private http: HttpClient,
    private webSocket: WebSocketService
  ) {
    merge(
      this.pollOperationFromServer()
        .pipe(tap((operation) => {
          if (operation) {
            console.info('🚒️ New operation (polled):', operation.keyword);
          } else if (operation === null) {
            console.info('🚒️ Currently no active operation (polled).');
          } else {
            console.error('Unexpected operation (polled):', operation);
          }
        })),
      this.webSocket.subscribe<Operation>('/notification/operation')
        .pipe(tap((operation) => {
          if (operation) {
            console.info('🚒️ New operation (pushed):', operation.keyword);
          } else if (operation === null) {
            console.info('⏰ Operation timeout over… unset active operation');
          } else {
            console.error('Unexpected operation (pushed):', operation);
          }
        }))
    ).subscribe((operation) => this.activeOperation$.next(operation));
  }

  private pollOperationFromServer(): Observable<Operation> {
    return this.http.get<Operation>(`${environment.hostUrl}/operation`);
  }

  public getActiveOperation(): Observable<Operation> {
    return this.activeOperation$
      .asObservable();
  }

  public isActiveOperation(): Observable<boolean> {
    return this.activeOperation$
      .asObservable()
      .pipe(map(operation => !!operation));
  }
}
