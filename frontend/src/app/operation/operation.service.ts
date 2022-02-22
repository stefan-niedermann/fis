import {Injectable} from '@angular/core'
import {BehaviorSubject, merge, Observable, Subject} from 'rxjs'
import {WebSocketService} from '../web-socket.service'
import {map, tap} from 'rxjs/operators'
import {HttpClient} from '@angular/common/http'
import {DefaultService, Operation} from "../gen";

@Injectable({
  providedIn: 'root'
})
export class OperationService {

  private readonly activeOperation$: Subject<Operation | null> = new BehaviorSubject<Operation | null>(null)

  constructor(
    private readonly http: HttpClient,
    private readonly webSocket: WebSocketService,
    private readonly apiService: DefaultService
  ) {
    merge(
      this.apiService.operationGet('response')
        .pipe(map((operation) => {
          if(operation.status === 204) {
            console.info('🚒️ New operation (polled):', operation.body)
            return operation.body
          } else if (operation.status === 204) {
            console.info('🚒️ Currently no active operation (polled).')
            return null
          } else {
            console.error('Unexpected operation (polled):', operation)
            return null
          }
        })),
      this.webSocket.subscribe<Operation>('/notification/operation')
        .pipe(tap((operation) => {
          if (operation) {
            console.info('🚒️ New operation (pushed):', operation.keyword)
          } else if (operation === null) {
            console.info('⏰ Operation timeout over… unset active operation')
          } else {
            console.error('Unexpected operation (pushed):', operation)
          }
        }))
    ).subscribe({
      next: operation => this.activeOperation$.next(operation),
      error: error => console.error(error)
    })
  }

  public getActiveOperation(): Observable<Operation | null> {
    return this.activeOperation$
      .asObservable()
  }

  public isActiveOperation(): Observable<boolean> {
    return this.activeOperation$
      .asObservable()
      .pipe(map(operation => !!operation))
  }
}
