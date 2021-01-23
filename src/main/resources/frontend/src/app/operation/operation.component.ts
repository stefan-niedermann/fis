import {Component, HostBinding, OnDestroy, OnInit} from '@angular/core';
import {OperationService} from "./operation.service";
import {Observable} from "rxjs";
import {Operation} from "../domain/operation";
import {ParameterService} from "../parameter.service";
import {map} from "rxjs/operators";
import {InfoService} from "../info/info.service";

@Component({
  selector: 'app-operation',
  templateUrl: './operation.component.html',
  styleUrls: ['./operation.component.scss']
})
export class OperationComponent implements OnInit, OnDestroy {

  operation$: Observable<Operation>;
  highlight$: Observable<boolean>;
  highlightTerm$: Observable<string>;

  @HostBinding('class.dark-theme') darkThemeClass: boolean = false;
  private darkThemeSubscription;

  constructor(
    private infoService: InfoService,
    private operationService: OperationService,
    private parameterService: ParameterService
  ) {
  }

  ngOnInit(): void {
    this.operation$ = this.operationService.getActiveOperation();
    this.highlightTerm$ = this.parameterService.getParameter()
      .pipe(map(parameter => parameter.operation.highlight));
    this.highlight$ = this.highlightTerm$
      .pipe(map(term => !!term));
    this.darkThemeSubscription = this.infoService.isDarkTheme()
      .subscribe(isDarkTheme => {
        this.darkThemeClass = isDarkTheme;
      });
  }

  ngOnDestroy(): void {
    this.darkThemeSubscription.unsubscribe();
  }

}
