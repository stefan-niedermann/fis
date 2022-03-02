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
  // ftp('ls -all').then((result) => {
  //   console.log(result.code)
  //   console.log(result.stdout)
  //   console.log(result.stderr)
  // })
  // ftp(`rm -r ${Cypress.env('FTP_DIR')}/*`)
})

Cypress.Commands.add('sendFaxToFtpServer', (type) => {
  // cy.exec('ls -all').then((result) => {
  //   console.log(result.code)
  //   console.log(result.stdout)
  //   console.log(result.stderr)
  // })
  ftp(`put -O ${Cypress.env('FTP_DIR')} cypress/assets/${type}.pdf`)
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
  return cy.exec(`lftp -u ${Cypress.env('FTP_USER')},${Cypress.env('FTP_PASS')} -e "set ssl:verify-certificate no; ${command}; quit;" ${Cypress.env('FTP_HOST')}`)
}
