declare namespace Cypress {
  interface Chainable<Subject = any> {
    sendFaxToFtpServer(type: 'invalid' | 'thl' | 'brand'): Chainable<null>;

    verifyInfoScreen(temperature?: number): Chainable<null>;

    verifyClockPresent(timeout?: number): Chainable<null>;

    verifyWeatherPresent(temperature?: number): Chainable<null>;

    verifyProcessingScreenShown(): Chainable<null>;

    verifyOperationShown(type: 'thl' | 'brand'): Chainable<null>;
  }
}

let faxNumber = 0;

Cypress.Commands.add('sendFaxToFtpServer', (type: 'invalid' | 'thl' | 'brand') => {
  if (Cypress.env('FTP_HOST')) {
    cy.wait(1_000) // Force different timestamp
    ftp(`put -O ${Cypress.env('FTP_DIR')} cypress/assets/${type}.pdf -o ${type}-${++faxNumber}.pdf`)
  } else {
    switch (type) {
      case 'brand':
        cy.intercept('/api/operation', SAMPLE_OPERATION_BRAND)
        break;
      case 'thl':
        cy.intercept('/api/operation', SAMPLE_OPERATION_THL)
        break;
      case 'invalid':
        cy.intercept('/api/operation', {statusCode: 204})
        break;
    }
  }
})

Cypress.Commands.add('verifyInfoScreen', (temperature?: number) => {
  cy.verifyClockPresent()
  cy.verifyWeatherPresent(temperature)
})

Cypress.Commands.add('verifyClockPresent', (timeout?: number) => {
  if (timeout === undefined) {
    cy.contains('Uhr')
  } else {
    cy.contains('Uhr', {timeout})
  }
})

Cypress.Commands.add('verifyWeatherPresent', (temperature?: number) => {
  cy.contains(temperature === undefined ? '°' : `${temperature}°`)
})

Cypress.Commands.add('verifyProcessingScreenShown', () => {
  cy.contains('wird verarbeitet', {timeout: 40_000})
})

Cypress.Commands.add('verifyOperationShown', (type: 'thl' | 'brand') => {
  switch (type) {
    case 'brand':
      cy.contains('B3', {timeout: 120_000})
      break;
    case 'thl':
      cy.contains('THL', {timeout: 120_000})
      break;
  }
})

function ftp(command: string) {
  return cy.exec(`lftp -u ${Cypress.env('FTP_USER')},${Cypress.env('FTP_PASS')} -e "set ssl:verify-certificate no; ${command}; quit;" ${Cypress.env('FTP_HOST')}`)
}

const SAMPLE_OPERATION_THL = {
  "keyword": "THL UNWETTER",
  "number": "",
  "street": "Muster Straße",
  "location": "99999 Musterdorf - Mustergemeinde",
  "obj": "",
  "tags": [
    "T3523",
    "Unwetter",
    "Fahrzeug / sonstiger Gegenstand sichern"
  ],
  "vehicles": [
    "FF Musterdorf",
    "Musterdorf 48/1",
    "Musterkreis Land 3/1"
  ],
  "note": "XY MARKE farbe\nA BC 123\nFahrzeug steht in der Musterbach // PPerson sitzt im Fahrzeug ist aber nicht in Gefahr"
}

const SAMPLE_OPERATION_BRAND = {
  "keyword": "B 3",
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
