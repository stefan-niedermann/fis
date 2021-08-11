describe('My First Test', () => {
  it('Visits the initial project page', () => {
    cy.intercept({ method: 'GET', url: '/operation' }, {})
    cy.intercept({ method: 'GET', url: '/weather', }, {
      temperature: 19.5,
      icon: 'cloudy',
      isDay: true
    }
    )
    cy.intercept({ method: 'GET', url: '/socket/info*', }, {})

    cy.visit('/')
    cy.contains('Uhr')
  })

  it('Should fetch and display running operations on startup', () => {
    cy.intercept({ method: 'GET', url: '/operation', }, {
      "keyword": "B 1",
      "number": "",
      "street": "Musterstraße",
      "location": "99999 Musterdorf - Mustergemeinde",
      "obj": "",
      "tags": [
        "B1014",
        "im Freien",
        "Abfall-, Müll-, Papiercontainer"
      ],
      "vehicles": [
        "9.8.7 RH FF Musterwehr",
        "Musterwehr 24/1",
        "Musterkreis Land 7/8",
        "Musterkreis Land 9/5",
        "Mustergemeinde 14/5"
      ],
      "note": "Container qualmt leicht - vmtl. heiße Asche (sichtbar)\nim Gelände ehem. Brennerei"
    }
    )
    cy.intercept({ method: 'GET', url: '/weather', }, {})
    cy.intercept({ method: 'GET', url: '/parameter' }, {
      language: 'de',
      operation: {
        duration: 5,
        highlight: 'land'
      }
    }
    )
    cy.intercept({ method: 'GET', url: '/socket/info*', }, {})
    cy.intercept({ method: 'POST', url: '/socket/*', }, {})
    cy.visit('/')
    cy.contains('B1')
    cy.contains('Musterstraße')
    cy.contains('Musterdorf')
    cy.contains('Musterwehr')
  })

})