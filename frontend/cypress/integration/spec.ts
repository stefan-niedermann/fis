describe('My First Test', () => {
  it('Visits the initial project page', () => {
    cy.intercept(
      {
        method: 'GET',
        url: '/operation',
      },
      {}
    )
    cy.intercept(
      {
        method: 'GET',
        url: '/weather',
      },
      {}
    )
    cy.intercept(
      {
        method: 'GET',
        url: '/socket/info*',
      },
      {}
    )
    cy.visit('/')
    cy.contains('Uhr')
  })
})
