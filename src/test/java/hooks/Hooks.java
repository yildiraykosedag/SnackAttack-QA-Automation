package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.SkipException;
import utilities.ConfigReader;
import utilities.Driver;

public class Hooks {

    @Before("@needs-account")
    public void requireAccount() {
        if (!ConfigReader.has("user.email") || !ConfigReader.has("user.password")) {
            throw new SkipException("user.email / user.password tanımlı değil – senaryo atlandı.");
        }
    }

    @After("@ui")
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            byte[] png = ((TakesScreenshot) Driver.getDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.attach(png, "image/png", scenario.getName());
        }
        Driver.closeDriver();
    }
}
