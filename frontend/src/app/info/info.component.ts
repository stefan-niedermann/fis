import {Component} from '@angular/core';
import {InfoService} from "./info.service";
import {map} from "rxjs/operators";

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.scss']
})
export class InfoComponent {

  private readonly weather$ = this.infoService.getCurrentWeather()

  readonly iconUrl$ = this.weather$.pipe(map(weather => weather.icon))
  readonly temperature$ = this.weather$.pipe(map(weather => weather.temperature))
  readonly time$ = this.infoService.getCurrentTime()

  constructor(
    private infoService: InfoService
  ) {
  }
}
