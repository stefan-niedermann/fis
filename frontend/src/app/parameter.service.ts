import {Injectable} from '@angular/core'
import {
  BehaviorSubject,
  catchError,
  concatMap,
  distinctUntilChanged,
  EMPTY,
  filter,
  interval,
  of,
  startWith
} from 'rxjs'
import {map, share, switchMap, tap} from 'rxjs/operators'
import {DefaultService} from "./gen";

@Injectable({
  providedIn: 'root'
})
export class ParameterService {

  private readonly lastETag$ = new BehaviorSubject<string | undefined>(undefined)
  private readonly parameter$ = interval(60_000).pipe(
    startWith(0),
    switchMap(() => this.lastETag$.pipe(distinctUntilChanged())),
    concatMap(lastETag => this.apiService.parameterGet(lastETag, 'response')
      .pipe(
        catchError(resp => resp.status === 304 ? of(resp) : EMPTY),
        tap(resp => this.lastETag$.next(resp.headers.get('ETag') || undefined)),
        filter(resp => resp.body !== undefined),
        map(resp => resp.body),
      )
    ),
    tap(parameter => console.info('⚙️ Parameter:', `${parameter}°`)),
    share()
  )

  constructor(
    private apiService: DefaultService
  ) {
  }

  public getParameter() {
    return this.parameter$
  }
}
