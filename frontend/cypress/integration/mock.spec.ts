describe('JarFIS main screen (mocked backend)', () => {

  beforeEach(() => {
    cy.clearFtpServer();
    cy.intercept('/api/weather', sampleWeather)
    cy.intercept('/api/parameter', sampleParameter)
  })

  describe('Info Screen', () => {
    it('Should display the time and temperature', () => {
      cy.visit('/')
      cy.verifyInfoScreen(20)
    })

    it('Should handle ETags for weather', () => {
      cy.visit('/')
      cy.verifyInfoScreen(20)

      cy.intercept('/api/weather', {statusCode: 304})
      cy.verifyInfoScreen(20)

      cy.intercept('/api/weather', {statusCode: 304})
      cy.verifyInfoScreen(20)

      cy.intercept('/api/weather', {
        body: {
          temperature: 23,
          icon: 'fog',
          isDay: true
        }
      })
      cy.verifyInfoScreen(20)
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
      cy.sendFaxToFtpServer('brand')
      cy.visit('/')
      cy.verifyOperationShown('brand')
    })

    it('Should handle ETags for operations', () => {
      cy.visit('/')
      cy.verifyInfoScreen()

      cy.sendFaxToFtpServer('brand')
      cy.verifyOperationShown('brand')

      cy.intercept('GET', '/api/operation', {statusCode: 304})
      cy.verifyOperationShown('brand')

      cy.intercept('GET', '/api/operation', {statusCode: 304})
      cy.verifyOperationShown('brand')

      cy.clearFtpServer()
      cy.verifyInfoScreen()
    })
  })

  describe('Integration', () => {
    it('Info → Processing → Operation (Brand) → Info → Processing → Operation (THL) → Info', () => {
      cy.visit('/')
      cy.verifyInfoScreen()

      cy.intercept('GET', '/api/operation', {statusCode: 202})
      cy.verifyProcessingScreenShown()

      cy.sendFaxToFtpServer('brand')
      cy.verifyOperationShown('brand')

      cy.clearFtpServer()
      cy.verifyInfoScreen()

      cy.intercept('GET', '/api/operation', {statusCode: 202})
      cy.verifyProcessingScreenShown()

      cy.sendFaxToFtpServer('thl')
      cy.verifyOperationShown('thl')

      cy.clearFtpServer()
      cy.verifyInfoScreen()
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

})
