package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/** Bir önceki koşumda başarısız olan senaryoları tekrar koşar (target/failed_scenarios.txt). */
@CucumberOptions(
        features = "@target/failed_scenarios.txt",
        glue = {"stepdefinitions", "hooks"},
        plugin = {"pretty", "html:target/cucumber-reports/rerun.html"},
        monochrome = true
)
public class FailedRunner extends AbstractTestNGCucumberTests {
}
