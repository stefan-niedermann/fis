import {Injectable} from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {Observable, Subject, timer} from "rxjs";
import {Weather} from './domain/weather';
import {environment} from "../environments/environment";
import {Operation} from "./domain/operation";
import {take} from "rxjs/operators";
import {ParameterService} from "./parameter.service";

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private weather$: Subject<Weather> = new Subject<Weather>();
  private operation$: Subject<Operation> = new Subject<Operation>();

  constructor(
    private parameterService: ParameterService
  ) {
    this.parameterService
      .getParameter()
      .pipe(take(1))
      .subscribe(parameter => {
        const ws = Stomp.over(new SockJS(`${environment.hostUrl}/socket`));
        ws.connect({}, () => {
          ws.send(`${environment.hostUrl}/register`, {}, {});
          ws.subscribe('/notification/weather', message => {
            const weather = JSON.parse(message.body);
            console.info('⛅️ New weather:', weather);
            this.weather$.next(weather)
          });
          ws.subscribe('/notification/operation', message => {
            const operation = JSON.parse(message.body);
            console.info('🚒️ New operation:', operation);
            this.operation$.next(operation);
            // TODO reset timer when a new operation arrives
            timer(parameter.operation.duration).pipe(
              take(1)
            ).subscribe(_ => {
              console.debug('⏰ Timeout over… unset active operation.');
              this.operation$.next(null);
            })
          });
        }, error => {
          console.error(error);
        });
      });
  }

  public receiveCurrentWeather(): Observable<Weather> {
    return this.weather$.asObservable();
  }

  public receiveCurrentOperation(): Observable<Operation> {
    return this.operation$.asObservable();
  }
}
