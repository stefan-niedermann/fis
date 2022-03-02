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
  ftp('ls')
  // ftp(`rm -r ${Cypress.env('FTP_DIR')}/*`)
})

Cypress.Commands.add('sendFaxToFtpServer', (type) => {
  ftp(`mirror --reverse ../assets/${type}.pdf ${Cypress.env('FTP_DIR')} --verbose`)
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

function ftp(command: string) {
  cy.exec(`lftp -u ${Cypress.env('FTP_USER')},${Cypress.env('FTP_PASS')} -e "set ssl:verify-certificate no; ${command}; quit;" ${Cypress.env('FTP_HOST')}`)
}
