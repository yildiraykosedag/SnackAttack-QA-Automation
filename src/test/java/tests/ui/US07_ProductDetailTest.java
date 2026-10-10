package tests.ui;

import org.testng.SkipException;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.LoginPage;
import pages.ProductDetailModal;
import pages.ProductsPage;
import utilities.ConfigReader;
import utilities.ReusableMethods;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

/** US_07 – User herhangi bir ürünü seçebilmeli ve sepete ekleyebilmeli. */
public class US07_ProductDetailTest extends BaseUiTest {

    private static final String PRODUCT = "Klasik Burger 120g";

    @Test(description = "TC_01 – Ürün detayı görüntülenir")
    public void productDetailIsDisplayed() {
        ProductDetailModal modal = new ProductsPage().open().openProduct(PRODUCT);
        String text = modal.text();
        assertTrue(text.contains(PRODUCT));
        assertTrue(text.contains("Additions"));
        assertTrue(text.contains("Add to Cart"));
        assertEquals(modal.quantity(), 1);
    }

    @Test(description = "TC_02 – İlave seçilince fiyat güncellenmeli (bilinen bug: SCRUM-235)")
    public void additionUpdatesPrice() {
        ProductDetailModal modal = new ProductsPage().open().openProduct(PRODUCT);
        String before = modal.price();
        String classBefore = modal.additionClass("Ketçap");
        modal.toggleAddition("Ketçap");
        assertNotEquals(modal.additionClass("Ketçap"), classBefore, "İlave seçili görünmeli");
        assertNotEquals(modal.price(), before, "Fiyat ilave ücreti kadar artmalı");
    }

    @Test(description = "TC_04 – Seçili ilave kaldırılabilir")
    public void additionCanBeUnselected() {
        ProductDetailModal modal = new ProductsPage().open().openProduct(PRODUCT);
        String classBefore = modal.additionClass("Ketçap");
        modal.toggleAddition("Ketçap").toggleAddition("Ketçap");
        assertEquals(modal.additionClass("Ketçap"), classBefore);
    }

    @Test(description = "TC_05 / TC_06 – Miktar artırılabilir, 1'in altına düşmez")
    public void quantityBoundaries() {
        ProductDetailModal modal = new ProductsPage().open().openProduct(PRODUCT);
        modal.increase(2);
        assertEquals(modal.quantity(), 3);
        modal.decrease(3);
        assertEquals(modal.quantity(), 1, "Miktar 1'in altına düşmemeli");
    }

    @Test(description = "TC_07 – Giriş yapmış kullanıcı ilave ve miktarla ürünü sepete ekler")
    public void addToCartAsLoggedInUser() {
        if (!ConfigReader.has("user.email") || !ConfigReader.has("user.password")) {
            throw new SkipException("user.email / user.password tanımlı değil.");
        }
        new LoginPage().open().login(ConfigReader.get("user.email"), ConfigReader.get("user.password"));
        ReusableMethods.waitUrlContains("/dashboard");
        new CartPage().open().clearCart();

        ProductDetailModal modal = new ProductsPage().open().openProduct(PRODUCT);
        modal.toggleAddition("Ketçap").increase(1).addToCart();
        assertTrue(ReusableMethods.waitForBodyText("Item added successfully."));

        CartPage cart = new CartPage().open();
        assertTrue(cart.containsProduct(PRODUCT));
        assertTrue(cart.pageText().contains("Ketçap"));
        assertTrue(cart.pageText().contains("$160.00"), "(75 + 5) x 2 = $160.00");
        cart.clearCart();
    }

    @Test(description = "TC_08 – Misafir kullanıcı sepete eklediğinde bilgilendirilmeli (bilinen bug: SCRUM-36)")
    public void guestAddToCart() {
        ProductDetailModal modal = new ProductsPage().open().openProduct(PRODUCT);
        modal.addToCart();
        boolean informed = ReusableMethods.waitForBodyText("Item added successfully.")
                || ReusableMethods.bodyText().toLowerCase().contains("login");
        assertTrue(informed, "Ürün eklenmeli veya giriş yapılması gerektiği söylenmeli");
    }
}
