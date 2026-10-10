package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.ConfigReader;
import utilities.Driver;
import utilities.ReusableMethods;

/** Tüm sayfaların ortak davranışları: navigasyon, header linkleri, input doldurma. */
public abstract class BasePage {

    protected final WebDriver driver = Driver.getDriver();

    protected final By productsLink = By.xpath("//nav//a[@href='/products']");
    protected final By contactLink = By.xpath("//nav//a[@href='/contact']");
    protected final By viewCartIcon = By.cssSelector("[aria-label='View Cart'], [title='View Cart']");

    protected void open(String path) {
        String base = ConfigReader.get("base.url").replaceAll("/$", "");
        driver.get(base + path);
    }

    /** React kontrollü inputlarda güvenilir doldurma: temizle + yaz. */
    protected void type(By locator, String text) {
        WebElement input = ReusableMethods.waitVisible(locator);
        input.clear();
        if (text != null && !text.isEmpty()) {
            input.sendKeys(text);
        }
    }

    protected void click(By locator) {
        WebElement element = ReusableMethods.waitClickable(locator);
        ReusableMethods.scrollIntoView(element);
        try {
            element.click();
        } catch (org.openqa.selenium.ElementClickInterceptedException e) {
            // sabit header / toast gibi öğeler butonu örterse JS ile tıklanır
            ReusableMethods.jsClick(element);
        }
    }

    public String pageText() {
        return ReusableMethods.bodyText();
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    public void goToProducts() {
        click(productsLink);
    }

    public void goToContact() {
        click(contactLink);
    }

    public void goToCart() {
        click(viewCartIcon);
    }
}
