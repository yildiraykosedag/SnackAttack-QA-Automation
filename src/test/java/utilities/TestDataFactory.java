package utilities;

import net.datafaker.Faker;

import java.util.Locale;

/** Her koşumda benzersiz test datası üretir ({Email}, {Username}, {PhoneNumber} yer tutucuları). */
public final class TestDataFactory {

    private static final Faker FAKER = new Faker(Locale.ENGLISH);

    private TestDataFactory() {
    }

    public static String uniqueSuffix() {
        return String.valueOf(System.currentTimeMillis()).substring(5);
    }

    public static String firstName() {
        return FAKER.name().firstName();
    }

    public static String lastName() {
        return FAKER.name().lastName();
    }

    /** İletişim formu e-postayı en fazla 25 karakter kabul ediyor (SCRUM-237), bu yüzden kısa tutulur. */
    public static String shortEmail() {
        return "qa" + uniqueSuffix() + "@test.com";
    }

    public static String username() {
        return "qa_" + uniqueSuffix();
    }

    public static String trPhoneNumber() {
        return "532" + FAKER.number().digits(7);
    }

    public static String address() {
        return FAKER.address().streetAddress();
    }
}
