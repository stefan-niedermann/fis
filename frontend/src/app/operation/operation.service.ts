import {Injectable} from '@angular/core'
import {
  BehaviorSubject,
  catchError,
  concatMap,
  distinctUntilChanged,
  EMPTY,
  interval,
  Observable,
  of,
  startWith,
} from 'rxjs'
import {map, share, switchMap, tap} from 'rxjs/operators'
import {DefaultService, Operation} from "../gen";

@Injectable({
  providedIn: 'root'
})
export class OperationService {

  private readonly lastETag$ = new BehaviorSubject<string | undefined>(undefined)
  private readonly activeOperation$ = interval(1_000).pipe(
    startWith(0),
    switchMap(() => this.lastETag$.pipe(distinctUntilChanged())),
    concatMap(lastETag => this.apiService.operationGet(lastETag, 'response')
      .pipe(
        catchError(resp => resp.status === 304 ? of(resp) : EMPTY),
        tap(resp => this.lastETag$.next(resp.headers.get('ETag') || undefined)),
        tap(operation => {
          if (operation.status === 200) {
            console.info('🚒️ New operation (polled):', operation.body)
          } else if (operation.status === 204) {
            console.info('🚒️ Currently no active operation (polled).')
          }
        }),
        map(resp => resp.body),
      )
    ),
    share()
  )

  constructor(
    private readonly apiService: DefaultService
  ) {
  }

  public getActiveOperation(): Observable<Operation | null> {
    return this.activeOperation$
  }

  public isActiveOperation(): Observable<boolean> {
    return this.activeOperation$.pipe(map(operation => operation !== null))
  }
}
