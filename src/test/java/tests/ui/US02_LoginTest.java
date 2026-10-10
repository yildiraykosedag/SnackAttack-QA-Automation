package tests.ui;

import org.testng.SkipException;
import org.testng.annotations.Test;
import pages.LoginPage;
import utilities.ConfigReader;
import utilities.ReusableMethods;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/** US_02 – User sayfaya giriş yapabilmeli. Kabul kriteri metinleri birebir doğrulanır. */
public class US02_LoginTest extends BaseUiTest {

    @Test(description = "TC_01 – Geçerli bilgilerle giriş")
    public void loginWithValidCredentials() {
        if (!ConfigReader.has("user.email") || !ConfigReader.has("user.password")) {
            throw new SkipException("user.email / user.password configuration.properties içinde tanımlı değil.");
        }
        LoginPage page = new LoginPage().open();
        page.login(ConfigReader.get("user.email"), ConfigReader.get("user.password"));
        ReusableMethods.waitUrlContains("/dashboard");
        assertTrue(ReusableMethods.waitForBodyText("Customer Panel"), "Kullanıcı paneli görüntülenmeli");
    }

    @Test(description = "TC_02 – @ içermeyen email için 'Invalid email' (bilinen bug: SCRUM-33)")
    public void emailWithoutAtSign() {
        LoginPage page = new LoginPage().open();
        page.login("alican.com", "Aa1234567");
        assertEquals(page.messageUnder("Email"), LoginPage.AC_INVALID_EMAIL);
    }

    @Test(description = "TC_03 – '.' içermeyen email için 'Invalid email' (bilinen bug: SCRUM-236)")
    public void emailWithoutDot() {
        LoginPage page = new LoginPage().open();
        page.enterEmail("alican@mailcom").enterPassword("");
        page.clickLogin();
        assertEquals(page.messageUnder("Email"), LoginPage.AC_INVALID_EMAIL);
    }

    @Test(description = "TC_04 – Password boşsa 'Password is required' (bilinen bug: SCRUM-32)")
    public void emptyPassword() {
        LoginPage page = new LoginPage().open();
        page.enterEmail("qa@test.com").clickLogin();
        assertEquals(page.messageUnder("Password"), LoginPage.AC_PASSWORD_REQUIRED);
    }

    @Test(description = "TC_05 – Email ve Password boşken Login butonu pasif olmalı (bilinen bug: SCRUM-31)")
    public void loginButtonDisabledWhenEmpty() {
        LoginPage page = new LoginPage().open();
        assertFalse(page.isLoginButtonEnabled(), "Login butonu tıklanabilir olmamalı");
    }

    @Test(description = "TC_06 – Yalnızca Email girildiğinde Login butonu pasif olmalı (bilinen bug: SCRUM-31)")
    public void loginButtonDisabledWithOnlyEmail() {
        LoginPage page = new LoginPage().open();
        page.enterEmail("qa@test.com");
        assertFalse(page.isLoginButtonEnabled(), "Login butonu tıklanabilir olmamalı");
    }

    @Test(description = "TC_10 – Password alanı maskelenmeli")
    public void passwordIsMasked() {
        LoginPage page = new LoginPage().open();
        assertEquals(page.passwordInputType(), "password");
    }
}
