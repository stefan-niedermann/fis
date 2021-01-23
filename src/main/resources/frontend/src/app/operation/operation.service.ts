import {Injectable} from '@angular/core';
import {Observable, of} from "rxjs";
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
    this.operation$ = of({
      "keyword": "B 1",
      "number": "",
      "street": "Musterstraße",
      "location": "99999 Musterdorf - Mustergemeinde",
      "obj": "",
      "tags": [
        "B1014",
        "im Freien",
        "Abfall-, Müll-, Papiercontainer"
      ],
      "vehicles": [
        "9.8.7 RH FF Musterwehr",
        "Musterwehr 24/1",
        "Musterkreis Land 7/8",
        "Musterkreis Land 9/5",
        "Mustergemeinde 14/5"
      ],
      "note": "Container qualmt leicht - vmtl. heiße Asche (sichtbar)\nim Gelände ehem. Brennerei"
    });
      //this.webSocket.receiveCurrentOperation();
  }

  public getActiveOperation(): Observable<Operation> {
    return this.operation$;
  }

  public isActiveOperation(): Observable<boolean> {
    return this.operation$.pipe(map(operation => !!operation));
  }
}
