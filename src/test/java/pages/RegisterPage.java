package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import utilities.ReusableMethods;

import java.util.Map;

/** /register – Create an Account formu (US_01). */
public class RegisterPage extends BasePage {

    public static final String REQUIRED = "This field is required.";
    public static final String PASSWORDS_MUST_MATCH = "Passwords must match.";
    public static final String INVALID_PHONE = "Invalid phone number.";
    public static final String INVALID_EMAIL = "Invalid email address.";

    private final By firstName = By.id("firstName");
    private final By lastName = By.id("lastName");
    private final By email = By.id("email");
    private final By userName = By.id("userName");
    private final By password = By.id("password");
    private final By confirmPassword = By.id("confirmPassword");
    private final By address = By.id("address");
    private final By regionCode = By.id("regionCode");
    private final By phoneNumber = By.id("phoneNumber");
    private final By registerButton = By.xpath("//button[@type='submit' and normalize-space()='Register']");
    private final By signInLink = By.xpath("//a[@href='/login']");

    private final Map<String, By> fields = Map.of(
            "FirstName", firstName, "LastName", lastName, "Email", email, "Username", userName,
            "Password", password, "Confirm Password", confirmPassword, "Address", address,
            "Phone Number", phoneNumber, "Country Code", regionCode);

    public RegisterPage open() {
        open("/register");
        ReusableMethods.waitVisible(firstName);
        return this;
    }

    public RegisterPage fill(String fName, String lName, String mail, String user, String pass, String confirm,
                             String addr, String region, String phone) {
        type(firstName, fName);
        type(lastName, lName);
        type(email, mail);
        type(userName, user);
        type(password, pass);
        type(confirmPassword, confirm);
        type(address, addr);
        selectRegion(region);
        type(phoneNumber, phone);
        return this;
    }

    /** region: "TR", "US", "GB" veya boş (Select Country Code). */
    public void selectRegion(String region) {
        Select select = new Select(ReusableMethods.waitVisible(regionCode));
        if (region == null || region.isEmpty()) {
            // "Select Country Code" seçeneği disabled; varsayılan olarak seçili bırakılır
            return;
        }
        select.selectByValue(region);
    }

    public void clickRegister() {
        click(registerButton);
    }

    /** Alanın hemen altındaki doğrulama mesajını döndürür (yoksa boş). */
    public String errorUnder(String field) {
        WebElement input = driver.findElement(fields.get(field));
        WebElement container = input.findElement(By.xpath("./ancestor::div[.//label][1]"));
        String text = container.getText();
        for (String msg : new String[]{REQUIRED, PASSWORDS_MUST_MATCH, INVALID_PHONE, INVALID_EMAIL}) {
            if (text.contains(msg)) {
                return msg;
            }
        }
        return "";
    }

    public String inputType(String field) {
        return driver.findElement(fields.get(field)).getAttribute("type");
    }

    public boolean isOnRegisterPage() {
        return currentUrl().contains("/register");
    }

    public void clickSignIn() {
        click(signInLink);
    }
}
