/*
 * Prerequisites:
 * - JarFIS instance must be up and running
 * - lftp needs to be installed
 * - Following environment variables must be present:
 *   - FTP_HOST
 *   - FTP_DIR
 *   - FTP_USER
 *   - FTP_PASS
 */

describe('JarFIS main screen (production backend)', () => {

  beforeEach(() => {
    cy.clearFtpServer()
    cy.visit('/')
    cy.verifyClockPresent()
  })

  afterEach(() => {
    cy.verifyClockPresent(4_000)
  })

  describe('Info Screen', () => {
    it('Should display the time', () => {
      cy.verifyClockPresent()
    })
  })

  describe('Processing Screen', () => {
    it('Should display a processing screen after a fax has been send to the server', () => {
      cy.sendFaxToFtpServer('thl')
      cy.verifyProcessingScreenShown()
    })
  })

  describe('Operation Screen', () => {
    it('should display an incoming operation fax', () => {
      cy.sendFaxToFtpServer('thl')
      cy.verifyProcessingScreenShown()
      cy.verifyOperationShown('thl')
    })
  })

  describe('Integration', () => {
    it('should display a second operation fax incoming after a first operation fax has been displayed', () => {
      cy.sendFaxToFtpServer('thl')
      cy.verifyProcessingScreenShown()
      cy.verifyOperationShown('thl')

      cy.verifyClockPresent()

      cy.sendFaxToFtpServer('brand')
      cy.verifyProcessingScreenShown()
      cy.verifyOperationShown('brand')
    })

    it('should display a second operation fax incoming immediately while first operation fax is being processed', () => {
      cy.sendFaxToFtpServer('brand')
      cy.verifyProcessingScreenShown()
      cy.sendFaxToFtpServer('thl')
      cy.verifyOperationShown('thl')
    })
  })

  xit('should not display invalid faxes', () => {
    cy.sendFaxToFtpServer('invalid')
    cy.verifyClockPresent()
    cy.verifyProcessingScreenShown().should('not.exist')
    cy.verifyOperationShown('thl').should('not.exist')
    cy.verifyOperationShown('brand').should('not.exist')
  })
})
