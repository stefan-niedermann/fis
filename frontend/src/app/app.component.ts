import {Component} from '@angular/core';
import {OperationService} from "./operation/operation.service";
import {Observable} from "rxjs";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  activeOperation$: Observable<boolean>;

  constructor(
    private operationService: OperationService
  ) {
    this.activeOperation$ = this.operationService.isActiveOperation();
  }
}
