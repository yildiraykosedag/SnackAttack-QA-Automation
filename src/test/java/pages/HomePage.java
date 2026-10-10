package pages;

import org.openqa.selenium.By;

public class HomePage extends BasePage {

    private final By heroTitle = By.xpath("//*[contains(text(),'Snack Attack')]");
    private final By orderNowButton = By.xpath("//a[normalize-space()='Order Now'] | //button[normalize-space()='Order Now']");

    public HomePage open() {
        open("/");
        return this;
    }

    public boolean isLoaded() {
        return !driver.findElements(heroTitle).isEmpty();
    }

    public void clickOrderNow() {
        click(orderNowButton);
    }
}
