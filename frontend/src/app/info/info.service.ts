import {Injectable} from '@angular/core';
import {WebSocketService} from "../web-socket.service";
import {BehaviorSubject, merge, Observable, Subject, timer} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {Weather} from "src/app/domain/weather";
import {map, share, tap} from "rxjs/operators";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class InfoService {

  private readonly currentWeather$: Subject<Weather> = new BehaviorSubject<Weather>(null);

  constructor(
    private http: HttpClient,
    private webSocketService: WebSocketService
  ) {
    merge(
      this.pollWeatherFromServer()
        .pipe(tap((weather) => console.info('⛅️ New weather (polled):', `${weather.temperature}°`))),
      this.webSocketService.subscribeToRoute<Weather>('/notification/weather')
        .pipe(tap((weather) => console.info('⛅️ New weather (pushed):', `${weather.temperature}°`)))
    )
      .pipe(share())
      .subscribe(weather => {
        this.currentWeather$.next(weather);
      });
  }

  public isDarkTheme(): Observable<boolean> {
    return this.currentWeather$
      .asObservable()
      .pipe(map(weather => {
        return weather
          ? !weather.isDay
          : false
      }));
  }

  public getCurrentWeather(): Observable<Weather> {
    return this.currentWeather$.asObservable();
  }

  private pollWeatherFromServer(): Observable<Weather> {
    return this.http.get<Weather>(`${environment.hostUrl}/weather`);
  }

  public getCurrentTime(): Observable<Date> {
    return timer(0, 5000).pipe(map(_ => new Date()))
  }
}
