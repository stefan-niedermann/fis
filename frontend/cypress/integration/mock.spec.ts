describe('JarFIS main screen (mocked backend)', () => {

  beforeEach(() => {
    cy.intercept('GET', '/api/operation', {statusCode: 204})
    cy.intercept('/api/weather', sampleWeather)
    cy.intercept('/api/parameter', sampleParameter)
  })

  describe('Info Screen', () => {
    it('Should display the time', () => {
      cy.visit('/')
      cy.verifyClockShown()
    })

    it('Should display the temperature', () => {
      cy.visit('/')
      cy.verifyWeatherPresent(20)
    })

    it('Should handle ETags for weather', () => {
      cy.visit('/')
      cy.verifyClockShown()
      cy.verifyWeatherPresent(20)

      cy.intercept('/api/weather', {statusCode: 304})
      cy.verifyWeatherPresent(20)

      cy.intercept('/api/weather', {statusCode: 304})
      cy.verifyWeatherPresent(20)

      cy.intercept('/api/weather', {
        body: {
          temperature: 23,
          icon: 'fog',
          isDay: true
        }
      })
      cy.verifyWeatherPresent(23)
    })
  })

  describe('Processing Screen', () => {
    it('Should recognize already processing operations on startup', () => {
      cy.intercept('GET', '/api/operation', {statusCode: 202})
      cy.visit('/')
      cy.verifyProcessingScreenShown()
    })
  })

  describe('Operation Screen', () => {
    it('Should fetch and display already running operations on startup', () => {
      cy.intercept('/api/operation', sampleOperation)
      cy.visit('/')
      cy.verifyOperationShown('B1')
    })

    it('Should handle ETags for operations', () => {
      cy.visit('/')
      cy.verifyClockShown()

      cy.intercept('/api/operation', sampleOperation)
      cy.verifyOperationShown('B1')

      cy.intercept('GET', '/api/operation', {statusCode: 304})
      cy.verifyOperationShown('B1')

      cy.intercept('GET', '/api/operation', {statusCode: 304})
      cy.verifyOperationShown('B1')

      cy.intercept('GET', '/api/operation', {statusCode: 204})
      cy.verifyClockShown()
    })
  })

  describe('Integration', () => {
    it('Info → Processing → Operation → Info', () => {
      cy.visit('/')
      cy.verifyClockShown()

      cy.intercept('GET', '/api/operation', {statusCode: 202})
      cy.verifyProcessingScreenShown()

      cy.intercept('/api/operation', sampleOperation)
      cy.verifyOperationShown('B1')

      cy.intercept('GET', '/api/operation', {statusCode: 204})
      cy.verifyClockShown()
    })
  })

  const sampleWeather = {
    temperature: 19.5,
    icon: 'cloudy',
    isDay: true
  }

  const sampleParameter = {
    highlight: 'muster',
    weatherPollInterval: 1_000,
    operationPollInterval: 1_000
  }

  const sampleOperation = {
    "keyword": "B 1",
    "number": "",
    "street": "Musterstraße",
    "location": "99999 Musterdorf - Mustergemeinde",
    "obj": "",
    "tags": [
      "B1014",
      "im Freien",
      "Abfall-, Müll-, Papiercontainer"
    ],
    "vehicles": [
      "9.8.7 RH FF Musterwehr",
      "Musterwehr 24/1",
      "Musterkreis Land 7/8",
      "Musterkreis Land 9/5",
      "Mustergemeinde 14/5"
    ],
    "note": "Container qualmt leicht - vmtl. heiße Asche (sichtbar)\nim Gelände ehem. Brennerei"
  }

})
