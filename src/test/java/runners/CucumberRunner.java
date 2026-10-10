package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Tüm UI senaryolarını koşar. Etiket ile filtrelemek için:
 *   mvn test -Dsuite=testng-cucumber.xml -Dcucumber.filter.tags="@US06"
 * Gerçek veri oluşturan (@creates-data) senaryolar varsayılan olarak hariç tutulur.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepdefinitions", "hooks"},
        tags = "not @creates-data",
        plugin = {"pretty", "html:target/cucumber-reports/cucumber.html", "json:target/cucumber-reports/cucumber.json",
                "rerun:target/failed_scenarios.txt"},
        monochrome = true
)
public class CucumberRunner extends AbstractTestNGCucumberTests {
}
