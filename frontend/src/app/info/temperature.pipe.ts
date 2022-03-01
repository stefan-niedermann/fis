import {Pipe, PipeTransform} from '@angular/core'

@Pipe({
  name: 'temperature'
})
export class TemperaturePipe implements PipeTransform {

  transform(temperature: number): string {
    return temperature
      ? `${(Math.round(temperature * 100) / 100).toFixed((temperature > -10 && temperature < 10) ? 1 : 0)}°`
      : ''
  }

}
