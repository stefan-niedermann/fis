import {Component} from '@angular/core';
import {InfoService} from "./info.service";

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.scss']
})
export class InfoComponent {

  readonly iconUrl$ = this.infoService.getWeather('icon')
  readonly temperature$ = this.infoService.getWeather('temperature')
  readonly time$ = this.infoService.getCurrentTime()

  constructor(
    private infoService: InfoService
  ) {
  }
}
