# Changelog

## 1.0.0

- Update dependencies
- Mark as stable

## 0.5.0

- Active operations are remembered (when a new tab is opened while an operation is active, it will directly display it)
- Active operations are cancelled from the server side (through pushing `null` as new operation after the given
  duration)
- UI uses production build for smaller file size and correct environment configuration (solves a CORS issue)

## 0.4.0

- New configuration file (Breaking change!)
- New UI (based on Angular)

## 0.3.0

- Further parser enhancements and stabilizations
- Display operation object on UI if available

## 0.2.1

Enhance MittelfrankenSuedParser and added more test scenarios

## 0.2.0

Stability improvements and internal refactorings

## 0.1.0

First release