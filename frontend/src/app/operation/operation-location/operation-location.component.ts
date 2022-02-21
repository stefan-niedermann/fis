import {Component, Input} from '@angular/core'
import {Operation} from '../../domain/operation'

@Component({
  selector: 'app-operation-location',
  templateUrl: './operation-location.component.html',
  styleUrls: ['./operation-location.component.scss']
})
export class OperationLocationComponent {

  @Input()
  operation: Operation | undefined

}
