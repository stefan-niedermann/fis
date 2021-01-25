import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, Subject, timer} from "rxjs";
import {WebSocketService} from "../web-socket.service";
import {map, take} from "rxjs/operators";
import {Operation} from "../domain/operation";
import {ParameterService} from "../parameter.service";

@Injectable({
  providedIn: 'root'
})
export class OperationService {

  private readonly activeOperation$: Subject<Operation> = new BehaviorSubject<Operation>(null);

  constructor(
    private parameterService: ParameterService,
    private webSocket: WebSocketService
  ) {
    combineLatest([
      this.webSocket.subscribeToRoute<Operation>('/notification/operation'),
      this.parameterService.getParameter()
    ]).subscribe(([operation, parameter]) => {
      console.info('🚒️ New operation:', operation ? operation.keyword : operation);
      this.activeOperation$.next(operation);
      timer(parameter.operation.duration).pipe(
        take(1)
        // TODO reset timer when a new operation arrives
        // takeUntil(this.activeOperation$)
      ).subscribe(_ => {
        console.debug('⏰ Timeout over… unset active operation.');
        this.activeOperation$.next(null);
      })
      return operation;
    });
  }

  public getActiveOperation(): Observable<Operation> {
    return this.activeOperation$
      .asObservable();
  }

  public isActiveOperation(): Observable<boolean> {
    return this.activeOperation$
      .asObservable()
      .pipe(map(operation => !!operation));
  }
}
