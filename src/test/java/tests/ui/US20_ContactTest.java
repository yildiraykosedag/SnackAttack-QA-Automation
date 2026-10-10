package tests.ui;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.ContactPage;
import utilities.ReusableMethods;
import utilities.TestDataFactory;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/** US_20 – User sayfadan mesaj gönderebilmeli. */
public class US20_ContactTest extends BaseUiTest {

    private static final String NAME = "QA Tester";
    private static final String SUBJECT = "QA Otomasyon Testi";
    private static final String MESSAGE = "Bu mesaj otomasyon testi tarafından gönderilmiştir.";

    @Test(groups = "creates-data", description = "TC_01 – Geçerli datalarla mesaj gönderilir")
    public void sendValidMessage() {
        ContactPage page = new ContactPage().open();
        page.fill(NAME, TestDataFactory.shortEmail(), SUBJECT + " " + TestDataFactory.uniqueSuffix(), MESSAGE, true).send();
        assertTrue(ReusableMethods.waitForBodyText(ContactPage.SUCCESS_TEXT), "Mesaj başarıyla oluşturulmalı");
    }

    @DataProvider(name = "requiredFields")
    public Object[][] requiredFields() {
        return new Object[][]{{"TC_02", "name"}, {"TC_03", "email"}, {"TC_04", "subject"}, {"TC_05", "message"}};
    }

    @Test(dataProvider = "requiredFields", description = "Zorunlu alan boşsa tarayıcı doğrulaması mesajı engeller")
    public void emptyRequiredField(String tc, String fieldId) {
        ContactPage page = new ContactPage().open();
        String email = TestDataFactory.shortEmail();
        page.fill("name".equals(fieldId) ? "" : NAME, "email".equals(fieldId) ? "" : email,
                "subject".equals(fieldId) ? "" : SUBJECT, "message".equals(fieldId) ? "" : MESSAGE, true);
        page.send();
        assertEquals(page.firstInvalidField(), fieldId, tc);
        assertFalse(page.validationMessage(fieldId).isEmpty(), tc + " – doğrulama mesajı olmalı");
        assertFalse(page.pageText().contains(ContactPage.SUCCESS_TEXT));
    }

    @Test(description = "TC_06 – @ içermeyen email")
    public void emailWithoutAtSign() {
        ContactPage page = new ContactPage().open();
        page.fill(NAME, "alicantest.com", SUBJECT, MESSAGE, true).send();
        assertEquals(page.firstInvalidField(), "email");
        assertTrue(page.validationMessage("email").contains("@"));
    }

    @Test(description = "TC_07 – Onay kutucuğu işaretlenmezse gönderilmez")
    public void consentNotChecked() {
        ContactPage page = new ContactPage().open();
        page.fill(NAME, TestDataFactory.shortEmail(), SUBJECT, MESSAGE, false).send();
        assertEquals(page.firstInvalidField(), "consent");
    }

    @Test(groups = "creates-data", description = "25 karakterden uzun geçerli email ile mesaj gönderilebilmeli (bilinen bug: SCRUM-237)")
    public void longValidEmailIsAccepted() {
        ContactPage page = new ContactPage().open();
        page.fill(NAME, "qa.long.email." + TestDataFactory.uniqueSuffix() + "@example.com", SUBJECT, MESSAGE, true).send();
        assertFalse(ReusableMethods.waitForBodyText(ContactPage.VALIDATION_FAILED),
                "Geçerli formatta email için 'Validation failed' gösterilmemeli");
    }
}
