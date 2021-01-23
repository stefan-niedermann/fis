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

  public getCurrentWeather(): Observable<Weather> {
    return merge(
      this.pollWeatherFromServer(),
      this.webSocketService.receiveCurrentWeather()
    );
  }

  private pollWeatherFromServer(): Observable<Weather> {
    return this.http.get<Weather>(`${environment.hostUrl}/weather`);
  }

  public getCurrentTime(): Observable<string> {
    return timer(0, 5000).pipe(map(_ => {
      const date = new Date();
      const hours = date.getHours();
      const minutes = date.getMinutes();
      return `${hours < 10 ? `0${hours}` : hours}:${minutes < 10 ? `0${minutes}` : minutes} Uhr`;
    }))
  }
}
