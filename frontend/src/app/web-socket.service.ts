import {Injectable} from '@angular/core';
import * as Stomp from 'stompjs';
import {Client} from 'stompjs';
import * as SockJS from 'sockjs-client';
import {Observable, Subject} from "rxjs";
import {environment} from "../environments/environment";
import {switchMap} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private readonly ws$: Subject<Client>;

  constructor() {
    this.ws$ = new Subject<Client>();
    const client = Stomp.over(new SockJS(`${environment.hostUrl}/socket`));
    client.connect({}, () => {
      client.send(`${environment.hostUrl}/register`);
      this.ws$.next(client);
    }, error => console.error(error));
  }

  public subscribe<T>(route: string): Observable<T> {
    return this.ws$.pipe(
      switchMap(ws => {
        const tmp$: Subject<T> = new Subject<T>();
        ws.subscribe(route, message => tmp$.next(JSON.parse(message.body)));
        return tmp$;
      })
    )
  }
}
