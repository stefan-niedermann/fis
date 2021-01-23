import {Component} from '@angular/core';
import {OperationService} from "./operation/operation.service";
import {Observable} from "rxjs";
import {InfoService} from "./info/info.service";
import {map} from "rxjs/operators";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  darkTheme$: Observable<boolean>;
  activeOperation$: Observable<boolean>;

  constructor(
    private infoService: InfoService,
    private operationService: OperationService
  ) {
    this.activeOperation$ = this.operationService.isActiveOperation();
    this.darkTheme$ = this.infoService.getCurrentWeather()
      .pipe(map(weather => {
        return weather
          ? !weather.isDay
          : false
      }));
  }
}
