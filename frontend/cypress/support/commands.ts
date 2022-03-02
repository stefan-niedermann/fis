const ftp_user = 'ftp'
const ftp_password = 'ftp'
const ftp_host = 'localhost'

Cypress.Commands.add('clearFtpServer', () => {
  cy.exec(`lftp -u ${ftp_user},${ftp_password} -e "set ssl:verify-certificate no; rm -r ./; quit;" ${ftp_host}`)
})

Cypress.Commands.add('sendFaxToFtpServer', (type) => {
  cy.exec(`lftp -u ${ftp_user},${ftp_password} -e "set ssl:verify-certificate no; mirror --reverse ../assets/${type}.pdf ./ --verbose; quit;" ${ftp_host}`)
})
