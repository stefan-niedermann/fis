import {Pipe, PipeTransform} from '@angular/core'
import {Operation} from "../../gen";

@Pipe({
  name: 'operationLocation'
})
export class OperationLocationPipe implements PipeTransform {

  transform(operation: Operation): string {
    if(!operation) {
      return ''
    }
    const streetWithNumber = operation.number ? `${operation.street} ${operation.number}` : operation.street
    if (streetWithNumber && operation.location && operation.obj) {
      return `${operation.obj}, ${streetWithNumber} ${operation.location}`
    } else if (streetWithNumber && operation.location && !operation.obj) {
      return `${streetWithNumber} ${operation.location}`
    } else if (streetWithNumber && !operation.location && operation.obj) {
      return `${operation.obj} ${streetWithNumber}`
    } else if (!streetWithNumber && operation.location && operation.obj) {
      return `${operation.obj}, ${operation.location}`
    } else if (streetWithNumber && !operation.location && !operation.obj) {
      return streetWithNumber
    } else if (!streetWithNumber && operation.location && !operation.obj) {
      return operation.location
    } else if (!streetWithNumber && !operation.location && operation.obj) {
      return operation.obj
    }
    return ''
  }

}
