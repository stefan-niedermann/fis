import {Component, Input} from '@angular/core'
import {Operation} from "../../gen";

@Component({
  selector: 'app-operation-topic',
  templateUrl: './operation-topic.component.html',
  styleUrls: ['./operation-topic.component.scss']
})
export class OperationTopicComponent {

  @Input()
  operation: Operation | undefined

}
