import {Component, OnInit} from '@angular/core';
import {InfoService} from "./info.service";
import {Observable} from 'rxjs';
import {map} from "rxjs/operators";
import {Weather} from "../domain/weather";

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.scss']
})
export class InfoComponent implements OnInit {

  private weather$: Observable<Weather>;
  iconUrl$: Observable<string>;
  temperature$: Observable<string>;
  time$: Observable<string>;

  constructor(
    private weatherService: InfoService
  ) {
  }

  ngOnInit(): void {
    this.weather$ = this.weatherService.getCurrentWeather();
    this.temperature$ = this.weather$.pipe(map(weather =>
      weather
        ? `${(Math.round(weather.temperature * 100) / 100).toFixed((weather.temperature > -10 && weather.temperature < 10) ? 1 : 0)}°`
        : ''
    ));
    this.iconUrl$ = this.weather$.pipe(map(weather =>
      weather
        ? `assets/weather-icons/${weather.icon}.svg`
        : ''
    ));
    this.time$ = this.weatherService.getCurrentTime();
  }

}
