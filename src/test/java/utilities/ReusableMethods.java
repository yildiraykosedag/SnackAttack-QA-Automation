package utilities;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ReusableMethods {

    private ReusableMethods() {
    }

    private static WebDriverWait webDriverWait() {
        return new WebDriverWait(Driver.getDriver(), Duration.ofSeconds(ConfigReader.getInt("timeout", 15)));
    }

    public static WebElement waitVisible(By locator) {
        return webDriverWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitClickable(By locator) {
        return webDriverWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static void waitUrlContains(String fraction) {
        webDriverWait().until(ExpectedConditions.urlContains(fraction));
    }

    public static void jsClick(WebElement element) {
        ((JavascriptExecutor) Driver.getDriver()).executeScript("arguments[0].click();", element);
    }

    public static void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) Driver.getDriver()).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    public static String bodyText() {
        return Driver.getDriver().findElement(By.tagName("body")).getText();
    }

    /** Sayfada verilen metin görünene kadar bekler (toast / uyarı mesajları için). */
    public static boolean waitForBodyText(String text) {
        try {
            webDriverWait().until(d -> d.findElement(By.tagName("body")).getText().contains(text));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static Path takeScreenshot(String name) {
        try {
            byte[] png = ((TakesScreenshot) Driver.getDriver()).getScreenshotAs(OutputType.BYTES);
            String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path target = Path.of("screenshots", name.replaceAll("[^A-Za-z0-9_-]", "_") + "_" + stamp + ".png");
            Files.createDirectories(target.getParent());
            return Files.write(target, png);
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }
}
