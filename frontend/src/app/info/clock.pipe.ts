import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'clock'
})
export class ClockPipe implements PipeTransform {

  transform(date: Date): string {
    if (!(date instanceof Date)) {
      return ''
    }
    const hours = date.getHours()
    const minutes = date.getMinutes()
    return `${hours < 10 ? `0${hours}` : hours}:${minutes < 10 ? `0${minutes}` : minutes} Uhr`
  }

}
