describe('JarFIS main screen (mocked backend)', () => {

  beforeEach(() => {
    cy.intercept('/api/operation', '')
    cy.intercept('/api/weather', sampleWeather)
    cy.intercept('/api/parameter', sampleParameter)
    cy.intercept('/api/socket/*', {})
  })


  it('Visits the initial project page', () => {
    cy.visit('/')
    cy.verifyClockShown()
  })

  it('Should fetch and display running operations on startup', () => {
    cy.intercept('/api/operation', sampleOperation)
    cy.visit('/')
    cy.verifyOperationShown(sampleOperation)
  })

  xit('Gets an operation pushed', () => {
    cy.visit('/')
    cy.verifyClockShown()
    cy.wait(500)
    cy.intercept('/api/operation', sampleOperation)
    cy.wait(500)
    cy.verifyOperationShown(sampleOperation)
    cy.wait(500)
    cy.verifyClockShown()
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
