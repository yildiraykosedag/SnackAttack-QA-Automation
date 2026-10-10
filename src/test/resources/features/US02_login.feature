@US02 @ui
Feature: US_02 - User sayfaya giris yapabilmeli

  Background:
    Given user is on the login page

  @smoke @needs-account
  Scenario: TC_01 - Gecerli bilgilerle giris yapilir
    When user logs in with the configured user account
    Then customer panel is displayed

  @knownbug @SCRUM-33
  Scenario: TC_02 - @ icermeyen email icin "Invalid email" gorulur
    When user enters email "alican.com" and password "Aa1234567"
    And user clicks the login button
    Then "Invalid email" message is displayed under login "Email"

  @knownbug @SCRUM-32
  Scenario: TC_04 - Password bos birakilinca "Password is required" gorulur
    When user enters email "qa@test.com" and password ""
    And user clicks the login button
    Then "Password is required" message is displayed under login "Password"

  @knownbug @SCRUM-31
  Scenario: TC_05 - Bilgiler girilmeden Login butonu tiklanabilir olmamali
    Then login button is disabled

  Scenario: TC_10 - Password alani maskelenir
    Then login password field is masked
