import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-operation-vehicles',
  templateUrl: './operation-vehicles.component.html',
  styleUrls: ['./operation-vehicles.component.scss']
})
export class OperationVehiclesComponent implements OnInit {

  @Input()
  vehicles: string[];

  @Input()
  highlightTerm: string;

  constructor() {
  }

  ngOnInit(): void {
  }

}
