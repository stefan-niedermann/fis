import {Injectable} from '@angular/core'
import {
  BehaviorSubject,
  catchError,
  concatMap,
  distinctUntilChanged,
  EMPTY,
  filter,
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
  private readonly processing$ = new BehaviorSubject(false)
  private readonly activeOperation$ = interval(2_000).pipe(
    startWith(0),
    switchMap(() => this.lastETag$.pipe(distinctUntilChanged())),
    concatMap(lastETag => this.apiService.operationGet(lastETag, 'response')
      .pipe(
        catchError(resp => resp.status === 304 ? of(resp) : EMPTY),
        filter(resp => resp.status !== 304),
        tap(resp => this.processing$.next(resp.status === 202)),
        tap(resp => this.lastETag$.next(resp.headers.get('ETag') || undefined)),
        map(resp => resp.status === 200 ? resp.body : null),
      )
    ),
    distinctUntilChanged(),
    tap(operation => {
      if (operation === null) {
        console.info('🚒️ Currently no active operation.')
      } else {
        console.info('🚒️ Active operation:', operation)
      }
    }),
    share()
  )

  constructor(
    private readonly apiService: DefaultService
  ) {
  }

  public getActiveOperation(): Observable<Operation | null> {
    return this.activeOperation$
  }

  public isActiveOperation() {
    return this.activeOperation$.pipe(map(operation => operation !== null))
  }

  public isProcessing() {
    return this.processing$.asObservable()
  }
}
