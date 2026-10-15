package tests.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import pojos.ApiResponse;
import pojos.Product;
import pojos.ProductPage;
import utilities.ApiSpec;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.lessThan;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/** US_06 / US_07 – Ürün uç noktaları (POJO deserialization). */
public class ProductApiTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test(description = "GET /products/allProduct-client – sayfalı ürün listesi POJO'ya dönüştürülür")
    public void listProductsAsPojo() {
        ProductPage page = given().spec(ApiSpec.spec())
                .queryParam("search", "").queryParam("page", 0).queryParam("size", 50)
                .when().get("/products/allProduct-client")
                .then().statusCode(200).time(lessThan(5000L))
                .extract().as(ProductPage.class);

        // Ortak ortamda admin testleri ürün ekleyebildiği için sabit sayı yerine başlangıç verisi ve sayfa tutarlılığı doğrulanır.
        assertTrue(page.getTotalElements() >= 28L, "En az 28 başlangıç ürünü olmalı");
        assertEquals(page.getContent().size(), page.getTotalElements().intValue(), "Tek sayfada tüm ürünler gelmeli");
        assertTrue(page.getContent().stream().allMatch(p -> p.getPrice() != null && p.getPrice() > 0), "Tüm fiyatlar pozitif olmalı");
    }

    @Test(description = "Bilinen bug SCRUM-258: kategori seçilmeden eklenen ürün kullanıcı listesinde kategorisiz görünüyor")
    public void everyListedProductHasCategory() {
        ProductPage page = given().spec(ApiSpec.spec())
                .queryParam("search", "").queryParam("page", 0).queryParam("size", 50)
                .when().get("/products/allProduct-client")
                .then().statusCode(200)
                .extract().as(ProductPage.class);

        assertTrue(page.getContent().stream().allMatch(p -> !p.getCategories().isEmpty()), "Her ürünün kategorisi olmalı");
    }

    @Test(description = "GET /products/allProduct-client?search=burger – arama filtresi")
    public void searchProducts() {
        given().spec(ApiSpec.spec())
                .queryParam("search", "burger").queryParam("page", 0).queryParam("size", 20)
                .when().get("/products/allProduct-client")
                .then().statusCode(200)
                .body("content.name", everyItem(containsStringIgnoringCase("burger")))
                .body("content.name", hasItem("Klasik Burger 120g"));
    }

    @Test(description = "GET /products/byId/5 – ApiResponse<Product> generic deserialization")
    public void getProductById() throws Exception {
        String json = given().spec(ApiSpec.spec())
                .when().get("/products/byId/5")
                .then().statusCode(200).body("success", equalTo(true))
                .extract().asString();

        ApiResponse<Product> response = MAPPER.readValue(json, new TypeReference<ApiResponse<Product>>() {
        });
        Product product = response.getData();
        assertNotNull(product);
        assertEquals(product.getName(), "Klasik Burger 120g");
        assertEquals(product.getPrice(), 75.0);
        assertEquals(product.getCategories().get(0).getName(), "HAMBURGER");
        assertEquals(product.getAdditionsCategory().get(0).getName(), "Soslar");
        assertTrue(product.getAvailable() && product.getActive());
    }

    @Test(description = "GET /products/byId/99999 – olmayan ürün 404")
    public void getUnknownProduct() {
        given().spec(ApiSpec.spec()).when().get("/products/byId/99999").then().statusCode(404);
    }

    @Test(description = "Bilinen bug SCRUM-246: search parametresi olmadan 400 + Java NullPointerException mesajı dönüyor")
    public void listWithoutSearchParameter() {
        Response response = given().spec(ApiSpec.spec())
                .queryParam("page", 0).queryParam("size", 5)
                .when().get("/products/allProduct-client");
        assertEquals(response.statusCode(), 200, "search opsiyonel olmalı; yanıt: " + response.asString());
    }

    @Test(description = "Bilinen bug SCRUM-247: GET /products/populer-list 500 dönüyor")
    public void popularList() {
        Response response = given().spec(ApiSpec.spec()).when().get("/products/populer-list");
        assertEquals(response.statusCode(), 200, "Popüler ürün listesi dönmeli; yanıt: " + response.asString());
        List<Product> products = response.jsonPath().getList("data", Product.class);
        assertTrue(products.stream().allMatch(Product::getPopular));
    }
}
