import {Component} from '@angular/core'
import {OperationService} from './operation.service'
import {ParameterService} from '../parameter.service'
import {map} from 'rxjs/operators'

@Component({
  selector: 'app-operation',
  templateUrl: './operation.component.html',
  styleUrls: ['./operation.component.scss']
})
export class OperationComponent {
  private readonly keywords = ['DEKON', 'THL', 'ABC', 'INF', 'SON', 'RD', 'B']

  readonly darkThemeClass = 'dark-theme'
  readonly operation$ = this.operationService.getActiveOperation()
  readonly operationKeyword$ = this.operation$
    .pipe(map(operation => operation ? operation.keyword : ''))
  readonly operationClass$ = this.operationKeyword$
    .pipe(
      map(keyword => this.keywords.find(k => keyword.toUpperCase().startsWith(k)) || ''),
      map(keyword => keyword.toLowerCase())
    )
  readonly highlightTerm$ = this.parameterService.getParameter()
    .pipe(map(parameter => parameter.highlight))
  readonly highlight$ = this.highlightTerm$
    .pipe(map(term => !!term))

  constructor(
    private readonly operationService: OperationService,
    private readonly parameterService: ParameterService
  ) {
  }
}
