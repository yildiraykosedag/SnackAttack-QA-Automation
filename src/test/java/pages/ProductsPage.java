package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utilities.ReusableMethods;

import java.util.List;
import java.util.stream.Collectors;

/** /products – ürün listesi, kategori filtreleri, arama ve sayfalama (US_06). */
public class ProductsPage extends BasePage {

    public static final String NO_PRODUCT_MESSAGE = "Ürün bulunamadı.";

    private final By searchInput = By.cssSelector("input[placeholder='Enter a description']");
    private final By searchButton = By.xpath("//button[normalize-space()='Search']");
    private final By productTitles = By.xpath("//main//h1|//main//h2|//main//h3|//main//h4|//main//h5|//main//h6");
    private final By productCardCategoryTags = By.xpath("//main//button[not(@type='button')][contains(.,'Doner') or contains(.,'Burger') or contains(.,'Pizza') or contains(.,'Drinks') or contains(.,'Desserts') or contains(.,'Salads')]");

    public ProductsPage open() {
        open("/products");
        waitForProducts();
        return this;
    }

    public void waitForProducts() {
        ReusableMethods.waitVisible(searchInput);
        ReusableMethods.sleep(1200);
    }

    public List<String> productNames() {
        return driver.findElements(productTitles).stream().map(WebElement::getText).map(String::trim)
                .filter(t -> !t.isEmpty()).collect(Collectors.toList());
    }

    /** Ürün kartlarının altındaki kategori etiketleri (ör. "Doner"). */
    public List<String> cardCategoryTags() {
        // kategori seçimi URL'yi (?category=..) değiştirip listeyi yeniden yükler
        ReusableMethods.waitVisible(productCardCategoryTags);
        return driver.findElements(productCardCategoryTags).stream()
                .map(e -> e.getText().replaceAll("[^A-Za-z]", "").trim()).collect(Collectors.toList());
    }

    public void selectCategory(String name) {
        By category = By.xpath("//main//button[@type='button'][normalize-space(.)!='' and contains(normalize-space(.),'" + name + "')]");
        click(category);
        ReusableMethods.sleep(1500);
    }

    public void search(String text) {
        type(searchInput, text);
        click(searchButton);
        ReusableMethods.sleep(1800);
    }

    public void goToPage(String label) {
        By pager = By.xpath("//ul//a[contains(normalize-space(.),'" + label + "')] | //ul//span[contains(normalize-space(.),'" + label + "')]");
        WebElement el = ReusableMethods.waitVisible(pager);
        ReusableMethods.scrollIntoView(el);
        ReusableMethods.jsClick(el);
        ReusableMethods.sleep(1500);
    }

    /** Kart üzerindeki fiyat (ör. "$75.00"). */
    public String cardPrice(String productName) {
        WebElement card = driver.findElement(By.xpath("//*[normalize-space()='" + productName + "']/ancestor::div[.//img][1]"));
        return card.getText().lines().filter(l -> l.matches(".*\\d+\\.\\d{2}.*")).findFirst().orElse("").trim();
    }

    public ProductDetailModal openProduct(String productName) {
        WebElement img = ReusableMethods.waitVisible(By.cssSelector("img[alt='" + productName + "']"));
        ReusableMethods.scrollIntoView(img);
        try {
            img.click();
        } catch (org.openqa.selenium.ElementClickInterceptedException e) {
            // smooth scroll bitmeden tıklanırsa kart başka bir öğenin altında kalabiliyor
            ReusableMethods.jsClick(img);
        }
        return new ProductDetailModal();
    }
}
