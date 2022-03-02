describe('JarFIS main screen (production backend)', () => {

  describe('Given no file is present at the FTP server on startup', () => {

    beforeEach(() => {
      cy.clearFtpServer()
      cy.visit('/')
    })

    it('should display the clock', () => {
      cy.verifyClockShown()
    })

    it('should display an incoming operation fax', () => {
      cy.verifyClockShown()
      cy.sendFaxToFtpServer('thl')
      cy.verifyProcessingScreenShown()
      cy.verifyOperationShown({
        street: 'street'
      })
    })

    it('should display a second operation fax incoming after a first operation fax has been displayed', () => {
      cy.verifyClockShown()
      cy.sendFaxToFtpServer('thl')
      cy.verifyProcessingScreenShown()
      cy.verifyOperationShown({
        street: 'street'
      })
      cy.verifyClockShown()
      cy.sendFaxToFtpServer('brand')
      cy.verifyProcessingScreenShown()
      cy.verifyOperationShown({
        street: 'street'
      })
      cy.verifyClockShown()
    })

    it('should display a second operation fax incoming immediately while first operation fax is being processed', () => {
      cy.verifyClockShown()
      cy.sendFaxToFtpServer('thl')
      cy.verifyProcessingScreenShown()
      cy.sendFaxToFtpServer('thl')
      cy.verifyOperationShown({
        street: 'street' // TODO
      })
    })

    xit('should not display invalid faxes', () => {
      cy.verifyClockShown()
      cy.sendFaxToFtpServer('invalid')
      cy.verifyClockShown()
      cy.verifyProcessingScreenShown().should('not.exist')
      cy.verifyOperationShown({
        street: 'street'
      }).should('not.exist')
    })
  })
})
