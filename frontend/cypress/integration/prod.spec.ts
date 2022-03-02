describe('JarFIS main screen (production backend)', () => {

  beforeEach(() => {
    cy.clearFtpServer()
    cy.visit('/')
  })

  it('Visits the initial project page', () => {
    cy.verifyClockShown();
  })

  it('Should fetch and display running operations on startup', () => {
    cy.sendFaxToFtpServer('thl')
    cy.verifyProcessingScreenShown()
    cy.verifyOperationShown({
      street: 'street'
    })
  })
})
