import {Component} from '@angular/core';
import {OperationService} from "./operation.service";
import {Observable} from "rxjs";
import {Operation} from "../domain/operation";
import {ParameterService} from "../parameter.service";
import {map} from "rxjs/operators";

@Component({
  selector: 'app-operation',
  templateUrl: './operation.component.html',
  styleUrls: ['./operation.component.scss']
})
export class OperationComponent {
  private readonly keywords = ['DEKON', 'THL', 'ABC', 'INF', 'SON', 'RD', 'B'];

  readonly darkThemeClass = 'dark-theme';
  operation$: Observable<Operation>;
  operationKeyword$: Observable<string>;
  operationClass$: Observable<string>;
  highlight$: Observable<boolean>;
  highlightTerm$: Observable<string>;

  constructor(
    private operationService: OperationService,
    private parameterService: ParameterService
  ) {
    this.operation$ = this.operationService.getActiveOperation();
    this.operationKeyword$ = this.operation$
      .pipe(map(operation => operation ? operation.keyword : ''))
    this.operationClass$ = this.operationKeyword$
      .pipe(
        map(keyword => this.keywords.find(k => keyword.toUpperCase().startsWith(k)) || ''),
        map(keyword => keyword.toLowerCase())
      )
    this.highlightTerm$ = this.parameterService.getParameter()
      .pipe(map(parameter => parameter.operation.highlight));
    this.highlight$ = this.highlightTerm$
      .pipe(map(term => !!term));
  }
}
