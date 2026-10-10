package tests.api;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import pojos.CartItemRequest;
import utilities.ApiSpec;
import utilities.ConfigReader;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

/**
 * US_02 / US_05 / US_08 – Kimlik doğrulama ve sepet uç noktaları.
 * configuration.properties içinde user.email / user.password yoksa testler atlanır.
 */
public class AuthAndCartApiTest {

    @Test(description = "POST /auth/login – geçerli bilgilerle token döner")
    public void loginReturnsToken() {
        String token = ApiSpec.userToken();
        org.testng.Assert.assertTrue(token != null && token.split("\\.").length == 3, "JWT formatında token dönmeli");
    }

    @Test(dependsOnMethods = "loginReturnsToken", description = "GET /user/getAuthenticatedUser – giriş yapan kullanıcı bilgisi")
    public void authenticatedUser() {
        given().spec(ApiSpec.authSpec())
                .when().get("/user/getAuthenticatedUser")
                .then().statusCode(200)
                .body("object.email", equalTo(ConfigReader.get("user.email")))
                .body("object.userRole", equalTo("MEMBER"));
    }

    @Test(dependsOnMethods = "loginReturnsToken",
            description = "Bilinen güvenlik bug'ı SCRUM-248: getAuthenticatedUser yanıtında bcrypt şifre hash'i dönüyor")
    public void authenticatedUserDoesNotExposePassword() {
        given().spec(ApiSpec.authSpec())
                .when().get("/user/getAuthenticatedUser")
                .then().statusCode(200)
                .body("object.password", org.hamcrest.Matchers.nullValue());
    }

    @Test(dependsOnMethods = "loginReturnsToken", description = "GET /api/orders/listUserOrders – kullanıcının siparişleri sayfalı listelenir (US_09)")
    public void listUserOrders() {
        given().spec(ApiSpec.authSpec())
                .when().get("/api/orders/listUserOrders")
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("data.content", notNullValue())
                .body("data.content.paymentMethod", org.hamcrest.Matchers.everyItem(notNullValue()));
    }

    @Test(dependsOnMethods = "loginReturnsToken", description = "POST /carts/current/items – ilaveli ürün sepete eklenir")
    public void addItemToCart() {
        given().spec(ApiSpec.authSpec())
                .body(new CartItemRequest(5L, 2, Set.of(4L)))
                .when().post("/carts/current/items")
                .then().statusCode(200);

        given().spec(ApiSpec.authSpec())
                .when().get("/carts/current")
                .then().statusCode(200)
                .body("data.cartId", notNullValue())
                .body("data.items.productId", hasItem(5));
    }

    @Test(description = "GET /user/getAuthenticatedUser – token olmadan 401")
    public void userEndpointRequiresAuth() {
        given().spec(ApiSpec.spec()).when().get("/user/getAuthenticatedUser").then().statusCode(401);
    }

    @AfterClass(alwaysRun = true)
    public void clearCart() {
        if (ConfigReader.has("user.password")) {
            given().spec(ApiSpec.authSpec()).when().delete("/carts/current");
        }
    }
}
