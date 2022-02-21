import {OperationLocationPipe} from './operation-location.pipe'
import {Operation} from '../../domain/operation'

describe('OperationLocationPipe', () => {
  const pipe = new OperationLocationPipe()

  it('create an instance', () => {
    expect(pipe).toBeTruthy()
  })

  it('should return an empty string for falsy values', () => {
    expect(pipe.transform(false as any)).toEqual('')
    expect(pipe.transform(null as any)).toEqual('')
    expect(pipe.transform(undefined as any)).toEqual('')
    expect(pipe.transform(0 as any)).toEqual('')
    expect(pipe.transform({} as any)).toEqual('')
    expect(pipe.transform(NaN as any)).toEqual('')
  })

  it('should transform location, street, number and object of an operation into one string', () => {
    const complete = (operation: Partial<Operation>) => pipe.transform({...operation} as Operation)

    expect(complete({street: 'Samplestreet'})).toEqual('Samplestreet')
    expect(complete({street: 'Samplestreet', number: '15'})).toEqual('Samplestreet 15')
    expect(complete({location: 'Foo'})).toEqual('Foo')
    expect(complete({obj: 'Bar'})).toEqual('Bar')
  })
})
