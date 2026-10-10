package tests.e2e;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.ProductDetailModal;
import pages.ProductsPage;
import pojos.ApiResponse;
import pojos.Product;
import utilities.ApiSpec;
import utilities.DBUtils;
import utilities.Driver;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * E2E_01 – Bir ürünün adı, fiyatı ve kategorisi UI, API ve DB katmanlarında tutarlı olmalı.
 * UI (Selenium) -> API (Rest Assured, POJO) -> DB (JDBC)
 */
public class ProductConsistencyE2ETest {

    @DataProvider(name = "products")
    public Object[][] products() {
        return new Object[][]{{5L, "Klasik Burger 120g"}, {1L, "Et Döner 100g"}, {8L, "Pizza Margherita"}};
    }

    @Test(dataProvider = "products", description = "E2E_01 – Ürün bilgileri UI = API = DB")
    public void productIsConsistentAcrossLayers(long id, String name) {
        // 1) UI – ürün detayındaki fiyat
        ProductDetailModal modal = new ProductsPage().open().openProduct(name);
        double uiPrice = Double.parseDouble(modal.price().replaceAll("[^0-9.]", ""));
        assertTrue(modal.text().contains(name));

        // 2) API – /products/byId/{id}
        Product apiProduct = given().spec(ApiSpec.spec()).get("/products/byId/" + id)
                .then().statusCode(200).extract().as(new io.restassured.common.mapper.TypeRef<ApiResponse<Product>>() {
                }).getData();

        // 3) DB – product tablosu
        Map<String, Object> dbRow = DBUtils.queryOne("select name, price, available, active from product where id = ?", id);
        assertNotNull(dbRow, "DB'de ürün bulunmalı");

        assertEquals(apiProduct.getName(), name, "API adı");
        assertEquals(dbRow.get("name"), name, "DB adı");
        assertEquals(apiProduct.getPrice(), uiPrice, "UI fiyatı = API fiyatı");
        assertEquals(((Number) dbRow.get("price")).doubleValue(), uiPrice, "UI fiyatı = DB fiyatı");
        assertEquals(dbRow.get("available"), apiProduct.getAvailable(), "available DB = API");
        assertEquals(dbRow.get("active"), apiProduct.getActive(), "active DB = API");
    }

    @Test(description = "E2E_02 – Ürün sayısı UI (sayfalama), API (totalElements) ve DB'de aynı")
    public void productCountIsConsistent() {
        ProductsPage page = new ProductsPage().open();
        int uiCount = page.productNames().size();
        page.goToPage("Next");
        uiCount += page.productNames().size();
        page.goToPage("Next");
        uiCount += page.productNames().size();

        long apiCount = given().spec(ApiSpec.spec()).queryParam("search", "").queryParam("page", 0).queryParam("size", 10)
                .get("/products/allProduct-client").then().statusCode(200).extract().jsonPath().getLong("totalElements");
        long dbCount = DBUtils.count("select count(*) from product where active = true and available = true");

        assertEquals(uiCount, apiCount, "UI = API");
        assertEquals(apiCount, dbCount, "API = DB");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        Driver.closeDriver();
    }
}
