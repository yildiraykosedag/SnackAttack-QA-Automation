package utilities;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.Duration;

/** ThreadLocal tabanlı WebDriver yönetimi (paralel koşuma uygun). Sürücüler Selenium Manager ile otomatik indirilir. */
public final class Driver {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private Driver() {
    }

    public static WebDriver getDriver() {
        if (DRIVER.get() == null) {
            boolean headless = Boolean.parseBoolean(ConfigReader.get("headless"));
            WebDriver driver;
            switch (ConfigReader.get("browser").toLowerCase()) {
                case "edge" -> driver = new EdgeDriver();
                case "firefox" -> driver = new FirefoxDriver();
                default -> {
                    ChromeOptions options = new ChromeOptions();
                    // Chrome otomatik çevirisi uyarı metinlerini değiştirdiği için kapatılır.
                    options.addArguments("--lang=en-US", "--window-size=1920,1080",
                            "--disable-features=Translate", "--disable-search-engine-choice-screen");
                    if (headless) {
                        options.addArguments("--headless=new");
                    }
                    driver = new ChromeDriver(options);
                }
            }
            if (!headless) {
                // headless modda maximize pencereyi 800x600'e düşürür; --window-size kullanılır
                driver.manage().window().maximize();
            }
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
            DRIVER.set(driver);
        }
        return DRIVER.get();
    }

    public static void closeDriver() {
        if (DRIVER.get() != null) {
            DRIVER.get().quit();
            DRIVER.remove();
        }
    }
}
