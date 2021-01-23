import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {WebSocketService} from "../web-socket.service";
import {map} from "rxjs/operators";
import {Operation} from "../domain/operation";

@Injectable({
  providedIn: 'root'
})
export class OperationService {

  private readonly operation$: Observable<Operation>;

  constructor(
    private webSocket: WebSocketService
  ) {
    this.webSocket.receiveCurrentOperation();
  }

  public getActiveOperation(): Observable<Operation> {
    return this.operation$;
  }

  public isActiveOperation(): Observable<boolean> {
    return this.operation$.pipe(map(operation => !!operation));
  }
}
