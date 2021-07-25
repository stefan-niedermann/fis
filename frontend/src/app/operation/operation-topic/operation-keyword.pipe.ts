import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'operationKeyword'
})
export class OperationKeywordPipe implements PipeTransform {

  transform(keyword: string): string {
    if(!keyword) {
      return '';
    }
    const upperKeyword = keyword.toUpperCase();
    if (upperKeyword.startsWith('B') || upperKeyword.startsWith('THL')) {
      const splitted = upperKeyword.split(' ');
      if (splitted.length > 1) {
        if (splitted[1].length < 3) {
          return splitted[0] + splitted[1];
        } else {
          return splitted[0];
        }
      } else {
        return splitted[0];
      }
    } else {
      const splitted = upperKeyword.split(' ');
      if (splitted.length > 0) {
        if (splitted[0].length <= 3) {
          return splitted[0];
        } else {
          return splitted[0].substring(0, 3);
        }
      }
    }
    return '';
  }

}
