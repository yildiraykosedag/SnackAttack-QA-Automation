package tests.ui;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.RegisterPage;
import utilities.ReusableMethods;
import utilities.TestDataFactory;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/** US_01 – User sayfaya kayıt olabilmelidir. */
public class US01_RegisterTest extends BaseUiTest {

    private static final String PASSWORD = "Aa1234567";

    /** Geçerli tüm alanlar; negatif senaryolarda tek alan değiştirilir. */
    private String[] validData() {
        return new String[]{TestDataFactory.firstName(), TestDataFactory.lastName(), TestDataFactory.shortEmail(),
                TestDataFactory.username(), PASSWORD, PASSWORD, TestDataFactory.address(), "TR",
                TestDataFactory.trPhoneNumber()};
    }

    private void fill(RegisterPage page, String[] d) {
        page.fill(d[0], d[1], d[2], d[3], d[4], d[5], d[6], d[7], d[8]);
    }

    @DataProvider(name = "emptyFields")
    public Object[][] emptyFields() {
        // {TC, alan, data dizisindeki index}
        return new Object[][]{
                {"TC_02", "FirstName", 0}, {"TC_03", "LastName", 1}, {"TC_04", "Phone Number", 8},
                {"TC_05", "Email", 2}, {"TC_06", "Username", 3}, {"TC_07", "Password", 4},
                {"TC_08", "Confirm Password", 5}, {"TC_16", "Address", 6}
        };
    }

    @Test(dataProvider = "emptyFields", description = "Zorunlu alan boş bırakıldığında 'This field is required.' uyarısı")
    public void emptyRequiredFieldShowsRequiredMessage(String tc, String field, int index) {
        RegisterPage page = new RegisterPage().open();
        String[] data = validData();
        data[index] = "";
        if ("Password".equals(field)) {
            data[5] = "";
        }
        fill(page, data);
        page.clickRegister();

        assertEquals(page.errorUnder(field), RegisterPage.REQUIRED, tc + " – " + field + " uyarısı");
        assertTrue(page.isOnRegisterPage(), tc + " – kayıt gerçekleşmemeli");
    }

    @Test(description = "TC_09 – Confirm Password uyuşmazsa 'Passwords must match.'")
    public void confirmPasswordMismatch() {
        RegisterPage page = new RegisterPage().open();
        String[] data = validData();
        data[5] = "Aa1234568";
        fill(page, data);
        page.clickRegister();
        assertEquals(page.errorUnder("Confirm Password"), RegisterPage.PASSWORDS_MUST_MATCH);
    }

    @DataProvider(name = "invalidPhones")
    public Object[][] invalidPhones() {
        return new Object[][]{
                {"TC_10", "TR", "55512abcde"}, {"TC_11", "TR", "555*12#45!"}, {"TC_12", "TR", "55512"},
                {"TC_13", "TR", "555123456789012"}, {"TC_14", "US", "5321234567"}
        };
    }

    @Test(dataProvider = "invalidPhones", description = "Country Code / Phone Number uyuşmazlığında 'Invalid phone number.'")
    public void invalidPhoneNumber(String tc, String region, String phone) {
        RegisterPage page = new RegisterPage().open();
        String[] data = validData();
        data[7] = region;
        data[8] = phone;
        fill(page, data);
        page.clickRegister();
        assertEquals(page.errorUnder("Phone Number"), RegisterPage.INVALID_PHONE, tc);
    }

    @Test(description = "TC_15 – Country Code seçilmezse 'This field is required.'")
    public void countryCodeNotSelected() {
        RegisterPage page = new RegisterPage().open();
        String[] data = validData();
        data[7] = "";
        fill(page, data);
        page.clickRegister();
        assertEquals(page.errorUnder("Country Code"), RegisterPage.REQUIRED);
    }

    @Test(description = "TC_17 – Geçersiz email formatı")
    public void invalidEmailFormat() {
        RegisterPage page = new RegisterPage().open();
        String[] data = validData();
        data[2] = "alicanmail";
        fill(page, data);
        page.clickRegister();
        assertEquals(page.errorUnder("Email"), RegisterPage.INVALID_EMAIL);
    }

    @Test(description = "TC_19 – Password ve Confirm Password maskelenmeli (bilinen bug: SCRUM-30)")
    public void passwordFieldsAreMasked() {
        RegisterPage page = new RegisterPage().open();
        SoftAssert soft = new SoftAssert();
        soft.assertEquals(page.inputType("Password"), "password", "Password maskelenmeli");
        soft.assertEquals(page.inputType("Confirm Password"), "password", "Confirm Password maskelenmeli (SCRUM-30)");
        soft.assertAll();
    }

    /**
     * TC_01 – Geçerli datalar ile kayıt. Gerçek hesap oluşturduğu için yalnızca "creates-data" grubunda koşulur
     * (testng-all.xml bu grubu hariç tutar). Kabul kriteri: "Success" mesajı (bilinen bug: SCRUM-29).
     */
    @Test(groups = "creates-data", description = "TC_01 – Geçerli datalarla başarılı kayıt")
    public void successfulRegistration() {
        RegisterPage page = new RegisterPage().open();
        fill(page, validData());
        page.clickRegister();
        assertTrue(ReusableMethods.waitForBodyText("Success"), "Kayıt sonrası 'Success' mesajı görüntülenmeli");
    }
}
