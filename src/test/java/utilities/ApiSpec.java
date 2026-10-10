package utilities;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.SkipException;
import pojos.LoginRequest;

import static io.restassured.RestAssured.given;

/** Rest Assured ortak RequestSpecification ve token alma yardımcıları. */
public final class ApiSpec {

    private static String userToken;

    static {
        // Türkçe locale'de Rest Assured "failure" -> "FAİLURE" dönüşümü yüzünden 4xx/5xx yanıtlarda hata fırlatır (IDE koşumları için)
        java.util.Locale.setDefault(java.util.Locale.US);
    }

    private ApiSpec() {
    }

    public static RequestSpecification spec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.get("api.base.url"))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .log(LogDetail.METHOD)
                .log(LogDetail.URI)
                .build();
    }

    /** configuration.properties'teki aktif User hesabıyla token alır; bilgiler yoksa testi atlar. */
    public static String userToken() {
        if (userToken == null) {
            if (!ConfigReader.has("user.email") || !ConfigReader.has("user.password")) {
                throw new SkipException("user.email / user.password tanımlı değil – giriş gerektiren API testi atlandı.");
            }
            userToken = given().spec(spec())
                    .body(new LoginRequest(ConfigReader.get("user.email"), ConfigReader.get("user.password")))
                    .post("/auth/login")
                    .then().statusCode(200)
                    .extract().path("token");
        }
        return userToken;
    }

    public static RequestSpecification authSpec() {
        return new RequestSpecBuilder().addRequestSpecification(spec())
                .addHeader("Authorization", "Bearer " + userToken()).build();
    }
}
