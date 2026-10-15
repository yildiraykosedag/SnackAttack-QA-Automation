package tests.api;

import org.testng.annotations.Test;
import pojos.Category;
import utilities.ApiSpec;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/** US_17 – Kategori uç noktaları. */
public class CategoryApiTest {

    @Test(description = "GET /category – 6 başlangıç kategorisi aktif olarak listelenir")
    public void listCategories() {
        List<Category> categories = given().spec(ApiSpec.spec())
                .when().get("/category")
                .then().statusCode(200).body("success", equalTo(true))
                .extract().jsonPath().getList("data", Category.class);

        // Ortak ortamda admin testleri kategori ekleyebildiği için sabit sayı yerine başlangıç kategorileri doğrulanır.
        assertTrue(categories.size() >= 6, "En az 6 kategori olmalı");
        assertTrue(categories.stream().allMatch(Category::getActive), "Tüm kategoriler aktif olmalı");
        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
        assertTrue(names.containsAll(List.of("DONER", "HAMBURGER", "PIZZA", "ICECEKLER", "TATLILAR", "SALATALAR")), names.toString());
    }

    @Test(description = "GET /category/admin – yetkisiz erişim engellenmeli (bilinen güvenlik bug'ı SCRUM-244: 200 dönüyor)")
    public void adminCategoryRequiresAuth() {
        given().spec(ApiSpec.spec()).when().get("/category/admin").then().statusCode(401);
    }

    @Test(description = "GET /products/allProduct-admin – yetkisiz erişim engellenmeli (bilinen güvenlik bug'ı SCRUM-245: 200 dönüyor)")
    public void adminProductListRequiresAuth() {
        given().spec(ApiSpec.spec()).queryParam("search", "").queryParam("page", 0).queryParam("size", 1)
                .when().get("/products/allProduct-admin").then().statusCode(401);
    }

    @Test(description = "GET /addition – yetkisiz erişim engellenir")
    public void additionsRequireAuth() {
        given().spec(ApiSpec.spec()).when().get("/addition").then().statusCode(401);
    }
}
