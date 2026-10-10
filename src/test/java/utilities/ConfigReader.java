package utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * configuration.properties dosyasını okur. Aynı anahtar ortam değişkeni olarak
 * verilirse (ör. db.password -> DB_PASSWORD) ortam değişkeni önceliklidir.
 */
public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream("configuration.properties"), StandardCharsets.UTF_8)) {
            PROPERTIES.load(reader);
        } catch (IOException e) {
            System.err.println("configuration.properties bulunamadı – configuration.example.properties dosyasını kopyalayın.");
        }
    }

    private ConfigReader() {
    }

    public static String get(String key) {
        String env = System.getenv(key.toUpperCase().replace('.', '_'));
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) {
            return sys.trim();
        }
        String value = PROPERTIES.getProperty(key);
        return value == null ? "" : value.trim();
    }

    public static boolean has(String key) {
        return !get(key).isEmpty();
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        return value.isEmpty() ? defaultValue : Integer.parseInt(value);
    }
}
