@US01 @ui
Feature: US_01 - User sayfaya kayit olabilmelidir

  Background:
    Given user is on the register page

  @negative
  Scenario Outline: <tc> - <field> bos birakildiginda "This field is required." uyarisi gorulur
    When user fills the register form with valid data except "<field>" left empty
    And user clicks the register button
    Then "This field is required." message is displayed under "<field>"
    And user stays on the register page

    Examples:
      | tc    | field            |
      | TC_02 | FirstName        |
      | TC_03 | LastName         |
      | TC_04 | Phone Number     |
      | TC_05 | Email            |
      | TC_06 | Username         |
      | TC_08 | Confirm Password |
      | TC_16 | Address          |

  @negative
  Scenario: TC_09 - Confirm Password uyusmazliginda "Passwords must match." gorulur
    When user fills the register form with valid data and confirm password "Aa1234568"
    And user clicks the register button
    Then "Passwords must match." message is displayed under "Confirm Password"

  @negative
  Scenario Outline: <tc> - Gecersiz telefon numarasinda "Invalid phone number." gorulur
    When user fills the register form with valid data, country code "<region>" and phone "<phone>"
    And user clicks the register button
    Then "Invalid phone number." message is displayed under "Phone Number"

    Examples:
      | tc    | region | phone           |
      | TC_10 | TR     | 55512abcde      |
      | TC_11 | TR     | 555*12#45!      |
      | TC_12 | TR     | 55512           |
      | TC_13 | TR     | 555123456789012 |
      | TC_14 | US     | 5321234567      |

  @negative
  Scenario: TC_17 - Gecersiz email formatinda "Invalid email address." gorulur
    When user fills the register form with valid data and email "alicanmail"
    And user clicks the register button
    Then "Invalid email address." message is displayed under "Email"

  @knownbug @SCRUM-30
  Scenario: TC_19 - Password ve Confirm Password maskelenmeli
    Then "Password" field is masked
    And "Confirm Password" field is masked
