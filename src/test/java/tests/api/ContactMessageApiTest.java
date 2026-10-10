package tests.api;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import pojos.ContactMessageRequest;
import pojos.ContactMessageResponse;
import utilities.ApiSpec;
import utilities.TestDataFactory;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/** US_20 / US_21 – İletişim mesajı uç noktaları (Serialization + Deserialization). */
public class ContactMessageApiTest {

    @Test(groups = "creates-data", description = "POST /contactMessages/save – POJO ile mesaj oluşturulur")
    public void createContactMessage() {
        ContactMessageRequest request = new ContactMessageRequest("QA Api", TestDataFactory.shortEmail(),
                "QA API Test " + TestDataFactory.uniqueSuffix(), "Rest Assured ile oluşturulan test mesajı.");

        ContactMessageResponse response = given().spec(ApiSpec.spec())
                .body(request)                                  // Serialization (POJO -> JSON)
                .when().post("/contactMessages/save")
                .then().statusCode(201)
                .extract().as(ContactMessageResponse.class);    // Deserialization (JSON -> POJO)

        assertEquals(response.getStatus(), "CREATED");
        assertTrue(response.getMessage().contains("CREATED"), response.getMessage());
        ContactMessageResponse.ContactMessage created = response.getObject();
        assertNotNull(created.getId());
        assertEquals(created.getEmail(), request.getEmail());
        assertEquals(created.getSubject(), request.getSubject());
        assertEquals(created.getMessage(), request.getMessage());
    }

    @Test(description = "POST /contactMessages/save – zorunlu alanlar boşsa 400")
    public void missingRequiredFields() {
        given().spec(ApiSpec.spec())
                .body(new ContactMessageRequest("", "", "", ""))
                .when().post("/contactMessages/save")
                .then().statusCode(400);
    }

    @Test(description = "POST /contactMessages/save – name 20 karakteri aşarsa 400")
    public void nameTooLong() {
        given().spec(ApiSpec.spec())
                .body(new ContactMessageRequest("A".repeat(21), "qa@test.com", "Subject", "Message"))
                .when().post("/contactMessages/save")
                .then().statusCode(400);
    }

    @Test(groups = "creates-data", description = "Bilinen bug (SCRUM-237): 26 karakterlik geçerli email reddediliyor")
    public void validLongEmailIsAccepted() {
        Response response = given().spec(ApiSpec.spec())
                .body(new ContactMessageRequest("QA Api", "kosedagyildiray5@gmail.com", "Subject", "Message"))
                .when().post("/contactMessages/save");
        // Bug düzeltildiğinde 201 dönmeli; şu an 400 "Validation failed".
        assertEquals(response.statusCode(), 201, response.asString());
    }

    @Test(description = "GET /contactMessages/getAll – yetkisiz erişim engellenir (US_21)")
    public void listRequiresAuth() {
        given().spec(ApiSpec.spec()).when().get("/contactMessages/getAll").then().statusCode(401);
    }
}
