package pages;

import org.openqa.selenium.By;
import utilities.ReusableMethods;

/** /cart – My Cart (US_07 / US_08). */
public class CartPage extends BasePage {

    public static final String EMPTY_CART = "Your cart is empty.";

    private final By title = By.xpath("//h1[normalize-space()='My Cart' or normalize-space()='MY CART']");
    private final By clearCart = By.xpath("//button[normalize-space()='Clear Cart']");
    private final By checkout = By.xpath("//button[normalize-space()='Checkout']");
    private final By confirmClear = By.xpath("//*[@role='dialog']//button[normalize-space()='Clear Cart']");

    public CartPage open() {
        open("/cart");
        ReusableMethods.waitVisible(title);
        ReusableMethods.sleep(1500);
        return this;
    }

    public boolean isEmpty() {
        return pageText().contains(EMPTY_CART);
    }

    public boolean containsProduct(String name) {
        return pageText().contains(name);
    }

    /** "Total: $170.00" satırındaki tutar. */
    public String subtotal() {
        return pageText().lines().filter(l -> l.startsWith("$")).reduce((a, b) -> b).orElse("").trim();
    }

    public boolean isCheckoutVisible() {
        return !driver.findElements(checkout).isEmpty();
    }

    public void clearCart() {
        if (!driver.findElements(clearCart).isEmpty()) {
            click(clearCart);
            click(confirmClear);
            ReusableMethods.waitForBodyText(EMPTY_CART);
        }
    }
}
