import {Component, HostBinding, OnDestroy, OnInit} from '@angular/core';
import {InfoService} from "./info.service";
import {Observable} from 'rxjs';
import {map} from "rxjs/operators";
import {Weather} from "../domain/weather";

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.scss']
})
export class InfoComponent implements OnInit, OnDestroy {

  private weather$: Observable<Weather>;
  iconUrl$: Observable<string>;
  temperature$: Observable<number>;
  time$: Observable<Date>;

  @HostBinding('class.dark-theme') darkThemeClass: boolean = false;
  private darkThemeSubscription;

  constructor(
    private infoService: InfoService
  ) {
    this.darkThemeSubscription = this.infoService.isDarkTheme()
      .subscribe(isDarkTheme => {
        this.darkThemeClass = isDarkTheme;
      });
  }

  ngOnInit(): void {
    this.weather$ = this.infoService.getCurrentWeather();
    this.temperature$ = this.weather$.pipe(map(weather =>
      weather ? weather.temperature : null
    ));
    this.iconUrl$ = this.weather$.pipe(map(weather =>
      weather ? weather.icon : null
    ));
    this.time$ = this.infoService.getCurrentTime();
  }

  ngOnDestroy(): void {
    this.darkThemeSubscription.unsubscribe();
  }

}
