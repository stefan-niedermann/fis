import {Injectable} from '@angular/core'
import {WebSocketService} from '../web-socket.service'
import {filter, merge, Observable, ReplaySubject, Subject, timer} from 'rxjs'
import {HttpClient} from '@angular/common/http'
import {map, share, tap} from 'rxjs/operators'
import {DefaultService, Weather} from "../gen";

@Injectable({
  providedIn: 'root'
})
export class InfoService {

  private readonly currentWeather$: Subject<Weather> = new ReplaySubject<Weather>(1)

  constructor(
    private readonly http: HttpClient,
    private readonly webSocketService: WebSocketService,
    private readonly apiService: DefaultService
  ) {
    merge(
      this.apiService.weatherGet()
        .pipe(tap((weather) => console.info('⛅️ New weather (polled):', `${weather.temperature}°`))),
      this.webSocketService.subscribe<Weather>('/notification/weather')
        .pipe(tap((weather) => console.info('⛅️ New weather (pushed):', `${weather.temperature}°`)))
    )
      .pipe(
        filter(weather => typeof weather === 'object'),
        share()
      )
      .subscribe({
        next: weather => this.currentWeather$.next(weather),
        error: error => console.error(error)
      })
  }

  public isDarkTheme(): Observable<boolean> {
    return this.currentWeather$
      .asObservable()
      .pipe(map(weather => {
        return weather
          ? !weather.isDay
          : false
      }))
  }

  public getCurrentWeather(): Observable<Weather> {
    return this.currentWeather$.asObservable()
  }

  public getCurrentTime(): Observable<Date> {
    return timer(0, 5_000).pipe(map(_ => new Date()))
  }
}
