import {Component, OnInit} from '@angular/core';
import {OperationService} from "./operation.service";
import {Observable} from "rxjs";
import {Operation} from "../domain/operation";

@Component({
  selector: 'app-operation',
  templateUrl: './operation.component.html',
  styleUrls: ['./operation.component.scss']
})
export class OperationComponent implements OnInit {

  operation$: Observable<Operation>

  constructor(
    private operationService: OperationService
  ) { }

  ngOnInit(): void {
    this.operation$ = this.operationService.getActiveOperation();
  }

}
