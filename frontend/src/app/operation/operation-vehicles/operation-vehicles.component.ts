import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-operation-vehicles',
  templateUrl: './operation-vehicles.component.html',
  styleUrls: ['./operation-vehicles.component.scss']
})
export class OperationVehiclesComponent {

  @Input()
  vehicles: string[];

  @Input()
  highlightTerm: string;

}
