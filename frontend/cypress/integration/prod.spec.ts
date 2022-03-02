describe('JarFIS main screen (production backend)', () => {

  beforeEach(() => cy.clearFtpServer())

  it('Visits the initial project page', () => {
    cy.visit('/')
  })

  it('Should fetch and display running operations on startup', () => {
  })
})
