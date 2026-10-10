@US20 @ui
Feature: US_20 - User sayfadan mesaj gonderebilmeli

  Background:
    Given user is on the contact page

  @creates-data
  Scenario: TC_01 - Gecerli datalarla mesaj gonderilir
    When user fills the contact form with valid data and accepts consent
    And user clicks the send button
    Then contact message is created successfully

  @negative
  Scenario Outline: <tc> - <field> bos birakilirsa mesaj gonderilmez
    When user fills the contact form leaving "<field>" empty
    And user clicks the send button
    Then browser validation blocks the "<field>" field

    Examples:
      | tc    | field   |
      | TC_02 | name    |
      | TC_03 | email   |
      | TC_04 | subject |
      | TC_05 | message |

  @negative
  Scenario: TC_07 - Onay kutucugu isaretlenmezse mesaj gonderilmez
    When user fills the contact form with valid data without consent
    And user clicks the send button
    Then browser validation blocks the "consent" field
