import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {OperationComponent} from './operation/operation.component';
import {InfoComponent} from './info/info.component';
import {HttpClientModule} from "@angular/common/http";
import {OperationLocationPipe} from './operation/operation-location.pipe';
import {TemperaturePipe} from './info/temperature.pipe';
import {IconUrlPipe} from './info/icon-url.pipe';
import {ClockPipe} from './info/clock.pipe';

@NgModule({
  declarations: [
    AppComponent,
    OperationComponent,
    InfoComponent,
    OperationLocationPipe,
    TemperaturePipe,
    IconUrlPipe,
    ClockPipe
  ],
  imports: [
    BrowserModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
