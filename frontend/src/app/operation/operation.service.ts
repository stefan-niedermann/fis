import {Injectable} from '@angular/core'
import {
  BehaviorSubject,
  catchError,
  combineLatest,
  concatMap,
  distinctUntilChanged,
  EMPTY,
  filter,
  interval,
  Observable,
  of,
  shareReplay,
  startWith,
} from 'rxjs'
import {map, switchMap, tap} from 'rxjs/operators'
import {DefaultService, Operation} from "../gen";

@Injectable({
  providedIn: 'root'
})
export class OperationService {

  private readonly lastETag$ = new BehaviorSubject<string | undefined>(undefined)
  private readonly processing$ = new BehaviorSubject(false)
  private readonly activeOperation$: Observable<Operation | null> = interval(2_000).pipe(
    startWith(0),
    switchMap(() => this.lastETag$.pipe(distinctUntilChanged())),
    concatMap(lastETag => this.apiService.getOperation(lastETag, 'response')
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
    shareReplay(1)
  )

  constructor(
    private readonly apiService: DefaultService
  ) {
  }

  public getActiveOperation() {
    return this.activeOperation$
  }

  public isActiveOperation() {
    return combineLatest([this.activeOperation$, this.processing$])
      .pipe(
        map(([operation, processing]) => {
          return operation === null
            ? processing
              ? OperationState.PROCESSING
              : OperationState.VOID
            : OperationState.ACTIVE
        }),
        distinctUntilChanged()
      )
  }

  public isProcessing() {
    return this.processing$.asObservable()
  }
}

export enum OperationState {
  VOID,
  ACTIVE,
  PROCESSING
}
