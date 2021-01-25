import {Component, Input, OnInit} from '@angular/core';
import {Operation} from "../../domain/operation";

@Component({
  selector: 'app-operation-topic',
  templateUrl: './operation-topic.component.html',
  styleUrls: ['./operation-topic.component.scss']
})
export class OperationTopicComponent implements OnInit {

  @Input()
  operation: Operation;

  constructor() {
  }

  ngOnInit(): void {
  }

}
