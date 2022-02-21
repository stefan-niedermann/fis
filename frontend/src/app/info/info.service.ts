import {Injectable} from '@angular/core'
import {WebSocketService} from '../web-socket.service'
import {filter, merge, Observable, ReplaySubject, Subject, timer} from 'rxjs'
import {HttpClient} from '@angular/common/http'
import {Weather} from 'src/app/domain/weather'
import {map, share, tap} from 'rxjs/operators'
import {environment} from '../../environments/environment'

@Injectable({
  providedIn: 'root'
})
export class InfoService {

  private readonly currentWeather$: Subject<Weather> = new ReplaySubject<Weather>(1)

  constructor(
    private readonly http: HttpClient,
    private readonly webSocketService: WebSocketService
  ) {
    merge(
      this.pollWeatherFromServer()
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

  private pollWeatherFromServer(): Observable<Weather> {
    return this.http.get<Weather>(`${environment.hostUrl}/weather`)
  }

  public getCurrentTime(): Observable<Date> {
    return timer(0, 5_000).pipe(map(_ => new Date()))
  }
}
