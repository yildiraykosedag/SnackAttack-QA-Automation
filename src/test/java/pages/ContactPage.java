package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import utilities.ReusableMethods;

/** /contact – iletişim formu (US_20). Uyarılar tarayıcının HTML5 doğrulama mesajlarıdır. */
public class ContactPage extends BasePage {

    public static final String SUCCESS_TEXT = "CREATED Successfully";
    public static final String VALIDATION_FAILED = "Validation failed";

    private final By name = By.id("name");
    private final By email = By.id("email");
    private final By subject = By.id("subject");
    private final By message = By.id("message");
    private final By consent = By.id("consent");
    private final By sendButton = By.xpath("//button[normalize-space()='Send']");

    public ContactPage open() {
        open("/contact");
        ReusableMethods.waitVisible(name);
        return this;
    }

    public ContactPage fill(String n, String e, String s, String m, boolean acceptConsent) {
        type(name, n);
        type(email, e);
        type(subject, s);
        type(message, m);
        WebElement box = driver.findElement(consent);
        if (box.isSelected() != acceptConsent) {
            ReusableMethods.jsClick(box);
        }
        return this;
    }

    public void send() {
        click(sendButton);
    }

    /** Formdaki ilk geçersiz alanın id'si ve tarayıcı doğrulama mesajı. */
    public String firstInvalidField() {
        for (By by : new By[]{name, email, subject, message, consent}) {
            WebElement el = driver.findElement(by);
            Boolean valid = (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checkValidity();", el);
            if (!valid) {
                return el.getAttribute("id");
            }
        }
        return "none";
    }

    public String validationMessage(String fieldId) {
        WebElement el = driver.findElement(By.id(fieldId));
        return (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].validationMessage;", el);
    }
}
