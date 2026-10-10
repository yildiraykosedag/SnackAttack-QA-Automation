package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.ContactPage;
import utilities.ReusableMethods;
import utilities.TestDataFactory;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ContactSteps {

    private static final String NAME = "QA Tester";
    private static final String SUBJECT = "QA Cucumber Testi";
    private static final String MESSAGE = "Bu mesaj Cucumber otomasyon testi tarafından gönderilmiştir.";

    private ContactPage contactPage;

    @Given("user is on the contact page")
    public void userIsOnTheContactPage() {
        contactPage = new ContactPage().open();
    }

    @When("user fills the contact form with valid data and accepts consent")
    public void fillValidWithConsent() {
        contactPage.fill(NAME, TestDataFactory.shortEmail(), SUBJECT + " " + TestDataFactory.uniqueSuffix(), MESSAGE, true);
    }

    @When("user fills the contact form with valid data without consent")
    public void fillValidWithoutConsent() {
        contactPage.fill(NAME, TestDataFactory.shortEmail(), SUBJECT, MESSAGE, false);
    }

    @When("user fills the contact form leaving {string} empty")
    public void fillLeavingEmpty(String field) {
        contactPage.fill("name".equals(field) ? "" : NAME,
                "email".equals(field) ? "" : TestDataFactory.shortEmail(),
                "subject".equals(field) ? "" : SUBJECT,
                "message".equals(field) ? "" : MESSAGE, true);
    }

    @When("user clicks the send button")
    public void clickSend() {
        contactPage.send();
    }

    @Then("contact message is created successfully")
    public void messageCreated() {
        assertTrue(ReusableMethods.waitForBodyText(ContactPage.SUCCESS_TEXT));
    }

    @Then("browser validation blocks the {string} field")
    public void validationBlocks(String field) {
        assertEquals(contactPage.firstInvalidField(), field);
        assertTrue(!contactPage.validationMessage(field).isEmpty());
    }
}
