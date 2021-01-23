import {Injectable} from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  constructor() { }

  public connect(): void {

    const socket = new SockJS('http://localhost:8080/socket');
    const ws = Stomp.over(socket);
    ws.connect({}, function(frame) {
      ws.send("/register", {}, {});
      ws.subscribe("/errors", function(message) {
        alert("Error " + message.body);
      });
      ws.subscribe("/notification/weather", function(message) {
        console.log('from sock', message);
      });
    }, function(error) {
      alert("STOMP error " + error);
    });
  }
}
