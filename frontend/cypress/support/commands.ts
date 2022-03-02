declare namespace Cypress {
  interface Chainable<Subject = any> {
    clearFtpServer(): Chainable<null>;
    sendFaxToFtpServer(type: 'invalid' | 'thl' | 'brand'): Chainable<null>;
    verifyClockShown(): Chainable<null>;
    verifyOperationShown(operation: any): Chainable<null>;
  }
}

const ftp_host = process.env.CYPRESS_FTP_HOST
const ftp_user = process.env.CYPRESS_FTP_USER
const ftp_pass = process.env.CYPRESS_FTP_PASS

Cypress.Commands.add('clearFtpServer', () => {
  cy.exec(`lftp -u ${ftp_user},${ftp_pass} -e "set ssl:verify-certificate no; rm -r ./; quit;" ${ftp_host}`)
})

Cypress.Commands.add('sendFaxToFtpServer', (type) => {
  cy.exec(`lftp -u ${ftp_user},${ftp_pass} -e "set ssl:verify-certificate no; mirror --reverse ../assets/${type}.pdf ./ --verbose; quit;" ${ftp_host}`)
})

Cypress.Commands.add('verifyClockShown', () => {
  cy.contains('Uhr')
})

Cypress.Commands.add('verifyOperationShown', (operation: any) => {
  cy.contains(operation.street)
})
