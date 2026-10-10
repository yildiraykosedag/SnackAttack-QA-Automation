package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utilities.ReusableMethods;

/** Ürün detay penceresi: ilaveler, miktar ve Add to Cart (US_07). */
public class ProductDetailModal extends BasePage {

    private final By dialog = By.cssSelector("[role='dialog']");
    private final By plus = By.xpath("//*[@role='dialog']//*[not(*) and normalize-space()='+']");
    private final By minus = By.xpath("//*[@role='dialog']//*[not(*) and normalize-space()='-']");
    private final By addToCart = By.xpath("//*[@role='dialog']//button[normalize-space()='Add to Cart']");

    public ProductDetailModal() {
        ReusableMethods.waitVisible(dialog);
    }

    public String text() {
        return driver.findElement(dialog).getText();
    }

    /** Detayda gösterilen fiyat (ör. "₺ 75.00"). */
    public String price() {
        return text().lines().filter(l -> l.contains("₺") && !l.contains("+")).findFirst().orElse("").trim();
    }

    public int quantity() {
        WebElement qty = driver.findElement(minus).findElement(By.xpath("./following-sibling::*[1]"));
        return Integer.parseInt(qty.getText().trim());
    }

    public ProductDetailModal increase(int times) {
        for (int i = 0; i < times; i++) {
            driver.findElement(plus).click();
            ReusableMethods.sleep(200);
        }
        return this;
    }

    public ProductDetailModal decrease(int times) {
        for (int i = 0; i < times; i++) {
            driver.findElement(minus).click();
            ReusableMethods.sleep(200);
        }
        return this;
    }

    private By addition(String name) {
        return By.xpath("//*[@role='dialog']//button[starts-with(normalize-space(),'" + name + "')]");
    }

    public ProductDetailModal toggleAddition(String name) {
        click(addition(name));
        ReusableMethods.sleep(300);
        return this;
    }

    public String additionClass(String name) {
        return driver.findElement(addition(name)).getAttribute("class");
    }

    public void addToCart() {
        click(addToCart);
    }

    public boolean isOpen() {
        return !driver.findElements(dialog).isEmpty();
    }
}
