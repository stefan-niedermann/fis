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
  cy.exec(`lftp -u ${Cypress.env('FTP_USER')},${Cypress.env('FTP_PASS')} -e "set ssl:verify-certificate no; rm -r ${Cypress.env('FTP_DIR')}/*; quit;" ${Cypress.env('FTP_HOST')}`)
})

Cypress.Commands.add('sendFaxToFtpServer', (type) => {
  cy.exec(`lftp -u ${Cypress.env('FTP_USER')},${Cypress.env('FTP_PASS')} -e "set ssl:verify-certificate no; mirror --reverse ../assets/${type}.pdf ${Cypress.env('FTP_DIR')} --verbose; quit;" ${Cypress.env('FTP_HOST')}`)
})

Cypress.Commands.add('verifyClockShown', () => {
  cy.contains('Uhr')
})

Cypress.Commands.add('verifyProcessingScreenShown', () => {
  cy.contains('wird verarbeitet')
})

Cypress.Commands.add('verifyOperationShown', (operation: any) => {
  cy.contains(operation.street)
})
