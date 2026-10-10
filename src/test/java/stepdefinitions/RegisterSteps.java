package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.RegisterPage;
import utilities.TestDataFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class RegisterSteps {

    private RegisterPage registerPage;

    private Map<String, String> validData() {
        Map<String, String> d = new LinkedHashMap<>();
        d.put("FirstName", TestDataFactory.firstName());
        d.put("LastName", TestDataFactory.lastName());
        d.put("Email", TestDataFactory.shortEmail());
        d.put("Username", TestDataFactory.username());
        d.put("Password", "Aa1234567");
        d.put("Confirm Password", "Aa1234567");
        d.put("Address", TestDataFactory.address());
        d.put("Country Code", "TR");
        d.put("Phone Number", TestDataFactory.trPhoneNumber());
        return d;
    }

    private void fill(Map<String, String> d) {
        registerPage.fill(d.get("FirstName"), d.get("LastName"), d.get("Email"), d.get("Username"), d.get("Password"),
                d.get("Confirm Password"), d.get("Address"), d.get("Country Code"), d.get("Phone Number"));
    }

    @Given("user is on the register page")
    public void userIsOnTheRegisterPage() {
        registerPage = new RegisterPage().open();
    }

    @When("user fills the register form with valid data except {string} left empty")
    public void fillExceptEmpty(String field) {
        Map<String, String> d = validData();
        d.put(field, "");
        fill(d);
    }

    @When("user fills the register form with valid data and confirm password {string}")
    public void fillWithConfirm(String confirm) {
        Map<String, String> d = validData();
        d.put("Confirm Password", confirm);
        fill(d);
    }

    @When("user fills the register form with valid data, country code {string} and phone {string}")
    public void fillWithPhone(String region, String phone) {
        Map<String, String> d = validData();
        d.put("Country Code", region);
        d.put("Phone Number", phone);
        fill(d);
    }

    @When("user fills the register form with valid data and email {string}")
    public void fillWithEmail(String email) {
        Map<String, String> d = validData();
        d.put("Email", email);
        fill(d);
    }

    @When("user clicks the register button")
    public void clickRegister() {
        registerPage.clickRegister();
    }

    @Then("{string} message is displayed under {string}")
    public void messageUnder(String message, String field) {
        assertEquals(registerPage.errorUnder(field), message);
    }

    @Then("user stays on the register page")
    public void staysOnRegister() {
        assertTrue(registerPage.isOnRegisterPage());
    }

    @Then("{string} field is masked")
    public void fieldIsMasked(String field) {
        assertEquals(registerPage.inputType(field), "password", field + " maskelenmeli");
    }
}
