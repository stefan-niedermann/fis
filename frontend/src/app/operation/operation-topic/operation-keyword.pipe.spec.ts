import {OperationKeywordPipe} from './operation-keyword.pipe';

describe('OperationKeywordPipe', () => {

  const pipe = new OperationKeywordPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return an empty string for falsy values', () => {
    expect(pipe.transform(false as any)).toEqual('');
    expect(pipe.transform(null as any)).toEqual('');
    expect(pipe.transform(undefined as any)).toEqual('');
    expect(pipe.transform(0 as any)).toEqual('');
    expect(pipe.transform('')).toEqual('');
    expect(pipe.transform(NaN as any)).toEqual('');
  })

  describe('case "B" and "THL"', () => {

    it('should strip any space separated additions to "B" and "THL" which are longer than two characters', () => {
      expect(pipe.transform('B 123')).toEqual('B');
      expect(pipe.transform('B 456')).toEqual('B');
      expect(pipe.transform('B foo bar')).toEqual('B');

      expect(pipe.transform('THL 123')).toEqual('THL');
      expect(pipe.transform('THL 456 b')).toEqual('THL');
      expect(pipe.transform('THL foo bar')).toEqual('THL');
    })

    it('should not strip the second part if it is one or two characters long', () => {
      expect(pipe.transform('B a')).toEqual('BA');
      expect(pipe.transform('B 13')).toEqual('B13');

      expect(pipe.transform('THL ba')).toEqual('THLBA');
      expect(pipe.transform('THL 4')).toEqual('THL4');
    })
  })

  it('should transform the given input to uppercase', () => {
    expect(pipe.transform('foo')).toEqual('FOO');
    expect(pipe.transform('Bar')).toEqual('BAR');
    expect(pipe.transform('BAZ')).toEqual('BAZ');
  })

  it('real world examples', () => {
    expect(pipe.transform('B 1')).toEqual('B1');
    expect(pipe.transform('B BMA')).toEqual('B');
    expect(pipe.transform('THL UNWETTER')).toEqual('THL');
    expect(pipe.transform('THL 1')).toEqual('THL1');
    expect(pipe.transform('THL P EINGESCHLOSSEN')).toEqual('THLP');
  })
});
