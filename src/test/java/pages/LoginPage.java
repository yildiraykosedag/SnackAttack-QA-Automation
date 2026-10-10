package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utilities.ReusableMethods;

/** /login – Login formu (US_02 / US_04). */
public class LoginPage extends BasePage {

    /** Kabul kriterindeki beklenen metinler. */
    public static final String AC_INVALID_EMAIL = "Invalid email";
    public static final String AC_PASSWORD_REQUIRED = "Password is required";

    private final By email = By.id("email");
    private final By password = By.id("password");
    private final By loginButton = By.xpath("//button[@type='submit' and normalize-space()='Login']");
    private final By forgotPassword = By.xpath("//a[@href='/forgot-password']");

    public LoginPage open() {
        open("/login");
        ReusableMethods.waitVisible(email);
        return this;
    }

    public LoginPage enterEmail(String value) {
        type(email, value);
        return this;
    }

    public LoginPage enterPassword(String value) {
        type(password, value);
        return this;
    }

    public void clickLogin() {
        click(loginButton);
    }

    public void login(String mail, String pass) {
        enterEmail(mail).enterPassword(pass).clickLogin();
    }

    public boolean isLoginButtonEnabled() {
        return driver.findElement(loginButton).isEnabled();
    }

    public String passwordInputType() {
        return driver.findElement(password).getAttribute("type");
    }

    /** E-posta veya şifre alanının bulunduğu bloktaki uyarı metni. */
    public String messageUnder(String field) {
        WebElement input = driver.findElement("Email".equalsIgnoreCase(field) ? email : password);
        // doğrulama mesajı input'un hemen ardından gelen <p> etiketinde gösterilir
        return input.findElements(By.xpath("./following-sibling::p")).stream()
                .map(WebElement::getText).map(String::trim).filter(t -> !t.isEmpty())
                .findFirst().orElse("");
    }

    public boolean isForgotPasswordVisible() {
        return driver.findElement(forgotPassword).isDisplayed();
    }
}
