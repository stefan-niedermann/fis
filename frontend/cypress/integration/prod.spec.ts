describe('JarFIS main screen (production backend)', () => {

  describe('Given no file is present at the FTP server on startup', () => {

    beforeEach(() => {
      cy.clearFtpServer()
      cy.visit('/')
    })

    describe('Info Screen', () => {
      it('Should display the time', () => {
        cy.visit('/')
        cy.verifyClockPresent()
      })
    })

    describe('Processing Screen', () => {
      it('Should display a processing screen after a fax has been send to the server', () => {
        cy.verifyClockPresent()
        cy.sendFaxToFtpServer('thl')
        cy.verifyProcessingScreenShown()
      })
    })

    describe('Operation Screen', () => {
      it('should display an incoming operation fax', () => {
        cy.verifyClockPresent()
        cy.sendFaxToFtpServer('thl')
        cy.verifyProcessingScreenShown()
        cy.verifyOperationShown('thl')
      })
    })

    describe('Integration', () => {
      it('should display a second operation fax incoming after a first operation fax has been displayed', () => {
        cy.verifyClockPresent()

        cy.sendFaxToFtpServer('thl')
        cy.verifyProcessingScreenShown()
        cy.verifyOperationShown('thl')

        cy.verifyClockPresent()

        cy.sendFaxToFtpServer('brand')
        cy.verifyProcessingScreenShown()
        cy.verifyOperationShown('brand')

        cy.verifyClockPresent()
      })

      it('should display a second operation fax incoming immediately while first operation fax is being processed', () => {
        cy.verifyClockPresent()
        cy.sendFaxToFtpServer('brand')
        cy.verifyProcessingScreenShown()
        cy.sendFaxToFtpServer('thl')
        cy.verifyOperationShown('thl')
      })
    })

    xit('should not display invalid faxes', () => {
      cy.verifyClockPresent()
      cy.sendFaxToFtpServer('invalid')
      cy.verifyClockPresent()
      cy.verifyProcessingScreenShown().should('not.exist')
      cy.verifyOperationShown('thl').should('not.exist')
      cy.verifyOperationShown('brand').should('not.exist')
    })
  })
})
