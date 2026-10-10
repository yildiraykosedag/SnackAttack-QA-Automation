package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.LoginPage;
import utilities.ConfigReader;
import utilities.ReusableMethods;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class LoginSteps {

    private LoginPage loginPage;

    @Given("user is on the login page")
    public void userIsOnTheLoginPage() {
        loginPage = new LoginPage().open();
    }

    @When("user logs in with the configured user account")
    public void loginWithConfiguredAccount() {
        loginPage.login(ConfigReader.get("user.email"), ConfigReader.get("user.password"));
    }

    @When("user enters email {string} and password {string}")
    public void enterCredentials(String email, String password) {
        loginPage.enterEmail(email).enterPassword(password);
    }

    @When("user clicks the login button")
    public void clickLogin() {
        loginPage.clickLogin();
    }

    @Then("customer panel is displayed")
    public void customerPanelIsDisplayed() {
        ReusableMethods.waitUrlContains("/dashboard");
        assertTrue(ReusableMethods.waitForBodyText("Customer Panel"));
    }

    @Then("{string} message is displayed under login {string}")
    public void messageUnderLoginField(String message, String field) {
        assertEquals(loginPage.messageUnder(field), message);
    }

    @Then("login button is disabled")
    public void loginButtonIsDisabled() {
        assertFalse(loginPage.isLoginButtonEnabled(), "Login butonu tıklanabilir olmamalı");
    }

    @Then("login password field is masked")
    public void passwordIsMasked() {
        assertEquals(loginPage.passwordInputType(), "password");
    }
}
