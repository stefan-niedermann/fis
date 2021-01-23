import {Injectable} from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {Observable, Subject} from "rxjs";
import {Weather} from './domain/weather';
import {environment} from "../environments/environment";
import {Operation} from "./domain/operation";

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private weather$: Subject<Weather> = new Subject<Weather>();
  private operation$: Subject<Operation> = new Subject<Operation>();

  constructor() {
    const socket = new SockJS(`${environment.hostUrl}/socket`);
    const ws = Stomp.over(socket);
    ws.connect({}, () => {
      ws.send(`${environment.hostUrl}/register`, {}, {});
      ws.subscribe(`${environment.hostUrl}/notification/weather`, message => {
        const weather = JSON.parse(message.body);
        console.info('⛅️ New weather:', weather);
        this.weather$.next(weather)
      });
      ws.subscribe(`${environment.hostUrl}/notification/operation`, message => {
        const operation = JSON.parse(message.body);
        console.info('🚒️ New operation:', operation);
        this.operation$.next(operation)
      });
    }, error => {
      console.error(error);
    })
  }

  public receiveCurrentWeather(): Observable<Weather> {
    return this.weather$.asObservable();
  }

  public receiveCurrentOperation(): Observable<Operation> {
    return this.operation$.asObservable();
  }
}
