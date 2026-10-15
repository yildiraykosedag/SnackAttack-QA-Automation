package tests.ui;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.ProductDetailModal;
import pages.ProductsPage;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/** US_06 – User ürünleri görebilmeli, listeleyebilmeli ve aratabilmeli. */
public class US06_ProductsTest extends BaseUiTest {

    @Test(description = "TC_01 – Tüm ürünler listelenir")
    public void allProductsAreListed() {
        ProductsPage page = new ProductsPage().open();
        List<String> names = page.productNames();
        assertEquals(names.size(), 10, "İlk sayfada 10 ürün listelenmeli");
        assertTrue(names.contains("Et Döner 100g"));
    }

    @Test(description = "TC_02 – Sayfalama (Next / Previous / Last / First)")
    public void pagination() {
        ProductsPage page = new ProductsPage().open();
        List<String> first = page.productNames();
        page.goToPage("Next");
        List<String> second = page.productNames();
        assertFalse(second.isEmpty());
        assertFalse(first.equals(second), "2. sayfa farklı ürünler içermeli");
        page.goToPage("Previous");
        assertEquals(page.productNames(), first);
        page.goToPage("Last");
        // Ortak ortamda yeni ürün eklenebildiği için son sayfanın içeriği sabit değil; boş olmaması ve ilk sayfadan farklı olması doğrulanır.
        List<String> last = page.productNames();
        assertFalse(last.isEmpty(), "Son sayfada ürün olmalı");
        assertFalse(last.equals(first), "Son sayfa ilk sayfadan farklı olmalı");
        page.goToPage("First");
        assertEquals(page.productNames(), first);
    }

    @DataProvider(name = "categories")
    public Object[][] categories() {
        return new Object[][]{{"TC_03", "Doner", "Et Döner 100g"}, {"TC_04", "Burger", "Klasik Burger 120g"},
                {"TC_05", "Pizza", "Pizza Margherita"}, {"TC_06", "Drinks", "Ayran 300ml"},
                {"TC_07", "Desserts", "Künefe"}, {"TC_08", "Salads", "Mevsim Salata"}};
    }

    @Test(dataProvider = "categories", description = "Kategori filtresi yalnızca ilgili ürünleri listeler")
    public void categoryFilter(String tc, String category, String expectedProduct) {
        ProductsPage page = new ProductsPage().open();
        page.selectCategory(category);
        List<String> tags = page.cardCategoryTags();
        assertFalse(tags.isEmpty(), tc + " – ürün listelenmeli");
        assertTrue(tags.stream().allMatch(category::equals), tc + " – tüm kartlar " + category + " olmalı: " + tags);
        assertTrue(page.productNames().contains(expectedProduct), tc);
    }

    @Test(description = "TC_09 – Açıklama ile arama (büyük/küçük harf duyarsız)")
    public void searchByDescription() {
        ProductsPage page = new ProductsPage().open();
        page.search("DÖNER");
        List<String> names = page.productNames();
        assertEquals(names.size(), 4);
        assertTrue(names.stream().allMatch(n -> n.contains("Döner")));
    }

    @Test(description = "TC_10 – Ürün adı ile arama")
    public void searchByName() {
        ProductsPage page = new ProductsPage().open();
        page.search("Pizza Margherita");
        assertEquals(page.productNames(), List.of("Pizza Margherita"));
    }

    @Test(description = "TC_11 – Bulunmayan ürün aratılınca bilgilendirme")
    public void searchNoResult() {
        ProductsPage page = new ProductsPage().open();
        page.search("xyzqwe123");
        assertTrue(page.productNames().isEmpty());
        assertTrue(page.pageText().contains(ProductsPage.NO_PRODUCT_MESSAGE));
    }

    @Test(description = "TC_13 – Kart ve detay aynı para birimini göstermeli (bilinen bug: SCRUM-35)")
    public void currencyIsConsistent() {
        ProductsPage page = new ProductsPage().open();
        String cardPrice = page.cardPrice("Klasik Burger 120g");
        ProductDetailModal modal = page.openProduct("Klasik Burger 120g");
        String detailPrice = modal.price();
        assertEquals(detailPrice.replaceAll("[\\d.,\\s]", ""), cardPrice.replaceAll("[\\d.,\\s]", ""),
                "Para birimi tutarlı olmalı: kart=" + cardPrice + " detay=" + detailPrice);
    }
}
