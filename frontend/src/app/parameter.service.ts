import {Inject, Injectable, InjectionToken} from '@angular/core'
import {
  BehaviorSubject,
  catchError,
  concatMap,
  distinctUntilChanged,
  EMPTY,
  filter,
  interval,
  of,
  shareReplay,
  startWith
} from 'rxjs'
import {map, switchMap, tap} from 'rxjs/operators'
import {DefaultService} from "./gen";

@Injectable({
  providedIn: 'root'
})
export class ParameterService {

  private readonly lastETag$ = new BehaviorSubject<string | undefined>(undefined)
  private readonly parameter$ = interval(this.pollInterval).pipe(
    startWith(0),
    switchMap(() => this.lastETag$.pipe(distinctUntilChanged())),
    concatMap(lastETag => this.apiService.getParameter(lastETag, 'response')
      .pipe(
        catchError(resp => resp.status === 304 ? of(resp) : EMPTY),
        tap(resp => this.lastETag$.next(resp.headers.get('ETag') || undefined)),
        filter(resp => resp.body !== undefined),
        map(resp => resp.body),
      )
    ),
    tap(parameter => console.info('⚙️ Parameter:', parameter)),
    shareReplay(1)
  )

  constructor(
    @Inject(POLL_INTERVAL_PARAMETER)
    private readonly pollInterval: number,
    private readonly apiService: DefaultService
  ) {
  }

  public getParameter() {
    return this.parameter$
  }
}

export const POLL_INTERVAL_PARAMETER = new InjectionToken<number>('POLL_INTERVAL_PARAMETER')
