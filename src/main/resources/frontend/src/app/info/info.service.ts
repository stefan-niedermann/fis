import {Injectable} from '@angular/core';
import {WebSocketService} from "../web-socket.service";
import {merge, Observable, timer} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {Weather} from "src/app/domain/weather";
import {map} from "rxjs/operators";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class InfoService {

  constructor(
    private http: HttpClient,
    private webSocketService: WebSocketService
  ) {
  }

  public isDarkTheme(): Observable<boolean> {
    return this.getCurrentWeather()
      .pipe(map(weather => {
        return weather
          ? !weather.isDay
          : false
      }));
  }

  public getCurrentWeather(): Observable<Weather> {
    return merge(
      this.pollWeatherFromServer(),
      this.webSocketService.receiveCurrentWeather()
    );
  }

  private pollWeatherFromServer(): Observable<Weather> {
    return this.http.get<Weather>(`${environment.hostUrl}/weather`);
  }

  public getCurrentTime(): Observable<Date> {
    return timer(0, 5000).pipe(map(_ => new Date()))
  }
}
