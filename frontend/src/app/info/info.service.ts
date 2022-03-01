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
  shareReplay,
  startWith,
  timer
} from 'rxjs'
import {map, switchMap, tap} from 'rxjs/operators'
import {DefaultService, Weather} from '../gen';
import {ParameterService} from '../parameter.service';

@Injectable({
  providedIn: 'root'
})
export class InfoService {

  private readonly lastETag$ = new BehaviorSubject<string | undefined>(undefined)
  private readonly weather$ = this.paramService.getParameter('weatherPollInterval').pipe(
    switchMap(pollInterval => interval(pollInterval)),
    startWith(0),
    switchMap(() => this.lastETag$.pipe(distinctUntilChanged())),
    concatMap(lastETag => this.apiService.getWeather(lastETag, 'response')
      .pipe(
        catchError(resp => resp.status === 304 ? of(resp) : EMPTY),
        tap(resp => this.lastETag$.next(resp.headers.get('ETag') || undefined)),
        filter(resp => resp.body !== undefined),
        map(resp => resp.body),
      )
    ),
    tap(weather => console.info('⛅️ Current weather:', `${weather?.temperature}°`)),
    shareReplay(1)
  )

  private readonly isDarkTheme$ = this.weather$.pipe(
    map(weather => !weather.isDay),
    distinctUntilChanged(),
    shareReplay(1)
  )

  private readonly currentTime$ = timer(0, 5_000).pipe(
    map(_ => new Date()),
    shareReplay(1)
  )

  constructor(
    private readonly paramService: ParameterService,
    private readonly apiService: DefaultService
  ) {
  }

  public getWeather(property?: keyof Weather) {
    return property
      ? this.weather$.pipe(
        map(weather => weather[property]),
        distinctUntilChanged()
      )
      : this.weather$
  }

  public isDarkTheme() {
    return this.isDarkTheme$
  }

  public getCurrentTime() {
    return this.currentTime$
  }
}
