import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'iconUrl'
})
export class IconUrlPipe implements PipeTransform {

  transform(icon: string): string {
    return icon
      ? `assets/weather-icons/${icon}.svg`
      : ''
  }

}
