declare namespace Cypress {
  interface Chainable<Subject = any> {
    clearFtpServer(): Chainable<null>;

    sendFaxToFtpServer(type: 'invalid' | 'thl' | 'brand'): Chainable<null>;

    verifyClockShown(): Chainable<null>;

    verifyProcessingScreenShown(): Chainable<null>;

    verifyOperationShown(operation: any): Chainable<null>;
  }
}

Cypress.Commands.add('clearFtpServer', () => {
  ftp(`mrm ${Cypress.env('FTP_DIR')}/*`)
})

Cypress.Commands.add('sendFaxToFtpServer', (type) => {
  ftp(`put -O ${Cypress.env('FTP_DIR')} cypress/assets/${type}.pdf`)
})

Cypress.Commands.add('verifyClockShown', () => {
  cy.contains('Uhr')
})

Cypress.Commands.add('verifyProcessingScreenShown', () => {
  cy.contains('wird verarbeitet', {timeout: 20_000})
})

Cypress.Commands.add('verifyOperationShown', (operation: any) => {
  cy.contains(operation.street, {timeout: 120_000})
})

function ftp(command: string) {
  return cy.exec(`lftp -u ${Cypress.env('FTP_USER')},${Cypress.env('FTP_PASS')} -e "set ssl:verify-certificate no; ${command}; quit;" ${Cypress.env('FTP_HOST')}`)
}
