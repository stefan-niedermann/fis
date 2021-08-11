describe('JarFIS main screen', () => {

  beforeEach(() => {
    cy.intercept('/operation', '')
    cy.intercept('/weather', sampleWeather)
    cy.intercept('/parameter', sampleParameter)
    cy.intercept('/socket/*', {})
  });


  it('Visits the initial project page', () => {
    cy.visit('/');
    verifyInfo();
  })

  it('Should fetch and display running operations on startup', () => {
    cy.intercept('/operation', sampleOperation)
    cy.visit('/');
    verifyOperation();
  })

  // FIXME how to mock the websocket connection?
  xit('Gets an operation pushed', () => {
    cy.visit('/');
    verifyInfo();
    // mockSocket.send(JSON.stringify(sampleOperation));
    verifyOperation();
    cy.wait(500)
    verifyInfo();
  })

  const sampleWeather = {
    temperature: 19.5,
    icon: 'cloudy',
    isDay: true
  }

  const sampleParameter = {
    language: 'de',
    operation: {
      duration: 5,
      highlight: 'land'
    }
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

  const verifyInfo = () => {
    cy.contains('Uhr')
  }

  const verifyOperation = () => {
    cy.contains(sampleOperation.street)
  }

})