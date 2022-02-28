import {BrowserModule} from '@angular/platform-browser'
import {NgModule} from '@angular/core'
import {RouterModule} from '@angular/router'

import {AppComponent} from './app.component'
import {OperationComponent} from './operation/operation.component'
import {InfoComponent} from './info/info.component'
import {HttpClientModule} from '@angular/common/http'
import {OperationLocationPipe} from './operation/operation-location/operation-location.pipe'
import {TemperaturePipe} from './info/temperature.pipe'
import {IconUrlPipe} from './info/icon-url.pipe'
import {ClockPipe} from './info/clock.pipe'
import {OperationKeywordPipe} from './operation/operation-topic/operation-keyword.pipe'
import {OperationTopicComponent} from './operation/operation-topic/operation-topic.component'
import {OperationVehiclesComponent} from './operation/operation-vehicles/operation-vehicles.component'
import {OperationLocationComponent} from './operation/operation-location/operation-location.component'
import {ApiModule} from "./gen";
import {OperationProcessingComponent} from './operation-processing/operation-processing.component';

@NgModule({
  declarations: [
    AppComponent,
    OperationComponent,
    InfoComponent,
    OperationLocationPipe,
    TemperaturePipe,
    IconUrlPipe,
    ClockPipe,
    OperationKeywordPipe,
    OperationTopicComponent,
    OperationVehiclesComponent,
    OperationLocationComponent,
    OperationProcessingComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    ApiModule,
    RouterModule.forRoot([
      {
        path: '',
        component: InfoComponent,
        pathMatch: 'full'
      },
      {
        path: 'operation',
        component: OperationComponent,
        pathMatch: 'full'
      },
      {
        path: 'operation-processing',
        component: OperationProcessingComponent,
        pathMatch: 'full'
      },
      {
        path: '*',
        redirectTo: '/'
      }
    ])
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
