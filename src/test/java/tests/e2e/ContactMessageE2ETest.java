package tests.e2e;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.ContactPage;
import pojos.ContactMessageRequest;
import pojos.ContactMessageResponse;
import utilities.ApiSpec;
import utilities.DBUtils;
import utilities.Driver;
import utilities.ReusableMethods;
import utilities.TestDataFactory;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * E2E_03 / E2E_04 – İletişim mesajı akışı (gerçek kayıt oluşturur, "creates-data" grubu).
 * UI'dan gönderilen mesaj DB'ye doğru yazılmalı; API'den oluşturulan mesaj DB'de aynı alanlarla bulunmalı.
 */
public class ContactMessageE2ETest {

    @Test(groups = "creates-data", description = "E2E_03 – UI'dan gönderilen mesaj DB'ye kaydedilir (Türkçe karakterler korunur)")
    public void uiMessageIsPersisted() {
        String email = TestDataFactory.shortEmail();
        String subject = "E2E Sipariş Sorusu " + TestDataFactory.uniqueSuffix();
        String message = "Ürünlerinizde şeker ve glüten var mı?";

        new ContactPage().open().fill("Yıldıray E2E", email, subject, message, true).send();
        assertTrue(ReusableMethods.waitForBodyText(ContactPage.SUCCESS_TEXT), "UI başarı mesajı");

        Map<String, Object> row = DBUtils.queryOne("select id, name, email, subject, message from contact_message "
                + "where email = ? and subject = ?", email, subject);
        assertNotNull(row, "Mesaj DB'de bulunmalı");
        assertEquals(row.get("name"), "Yıldıray E2E");
        assertEquals(row.get("message"), message);
    }

    @Test(groups = "creates-data", description = "E2E_04 – API ile oluşturulan mesaj DB'de aynı id ve alanlarla bulunur")
    public void apiMessageIsPersisted() {
        ContactMessageRequest request = new ContactMessageRequest("QA E2E Api", TestDataFactory.shortEmail(),
                "E2E API " + TestDataFactory.uniqueSuffix(), "API -> DB doğrulama mesajı");

        ContactMessageResponse response = given().spec(ApiSpec.spec()).body(request)
                .post("/contactMessages/save").then().statusCode(201).extract().as(ContactMessageResponse.class);
        Long id = response.getObject().getId();

        Map<String, Object> row = DBUtils.queryOne("select name, email, subject, message from contact_message where id = ?", id);
        assertNotNull(row, "id=" + id + " DB'de bulunmalı");
        assertEquals(row.get("email"), request.getEmail());
        assertEquals(row.get("subject"), request.getSubject());
        assertEquals(row.get("message"), request.getMessage());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        Driver.closeDriver();
    }
}
