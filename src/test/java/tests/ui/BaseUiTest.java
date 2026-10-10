package tests.ui;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import utilities.Driver;
import utilities.ReusableMethods;

/** Her UI testinden sonra: başarısızsa ekran görüntüsü al, tarayıcıyı kapat. */
public abstract class BaseUiTest {

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            ReusableMethods.takeScreenshot(result.getMethod().getMethodName());
        }
        Driver.closeDriver();
    }
}
