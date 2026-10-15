# Snack Attack – Selenium + TestNG (POM)

QA Bitirme Projesi – **Yıldıray Kösedağ** · Ödev maddesi **13**

Test edilen uygulama: <https://snackattack.deployedprojects.xyz/>

Bu repo, Java + Selenium + TestNG ile **Page Object Model** mimarisinde hazırlanmış UI test otomasyonu projesidir.
Bitirme projesinin Cucumber (BDD) + JUnit bölümü – UI senaryoları, Rest Assured + POJO API testleri, DB ve E2E testleri,
Postman koleksiyonu ve teslim belgeleri (Test Planı, test case'ler, Xray) – ayrı bir projededir:
<https://github.com/yildiraykosedag/SnackAttack-QA-Cucumber>

## Teknolojiler

Java 17+ · Maven · Selenium 4 · TestNG 7 · Datafaker

## Proje yapısı

```
SnackAttack-QA-TestNG
├── pom.xml
├── testng.xml                          # UI suite ("creates-data" grubu hariç)
├── configuration.example.properties    # kopyalayıp configuration.properties yapın (git'e gönderilmez)
└── src/test/java
    ├── pages/        BasePage, HomePage, RegisterPage, LoginPage, ProductsPage, ProductDetailModal, CartPage, ContactPage
    ├── tests/ui/     BaseUiTest, US01_RegisterTest, US02_LoginTest, US06_ProductsTest, US07_ProductDetailTest, US20_ContactTest
    └── utilities/    Driver (ThreadLocal WebDriver), ConfigReader, ReusableMethods, TestDataFactory
```

- **pages** – Locator'lar ve sayfa aksiyonları; testler doğrudan Selenium çağrısı yapmaz, page metodlarını kullanır.
- **tests/ui** – Her User Story için bir test sınıfı; veri odaklı senaryolar `@DataProvider` ile, çok adımlı kontroller `SoftAssert` ile.
- **BaseUiTest** – Her testten sonra başarısızsa ekran görüntüsünü `screenshots/` klasörüne kaydeder ve tarayıcıyı kapatır.
- **Driver** – ThreadLocal WebDriver (paralel koşuma uygun); ChromeDriver Selenium Manager ile otomatik indirilir, `headless` desteği vardır.

## Kurulum

1. JDK 17+, Maven 3.9+ ve Google Chrome kurulu olmalı.
2. `configuration.example.properties` dosyasını `configuration.properties` olarak kopyalayın; giriş gerektiren testler için
   `user.email` / `user.password` alanlarına aktif bir User test hesabı girin (boşsa bu testler **SKIP** olur).

## Koşum

```bash
mvn test                          # testng.xml
mvn test -Dheadless=true          # tarayıcıyı görünmez modda çalıştır
mvn test -Dbrowser=edge           # chrome (varsayılan) / edge / firefox
```

Rapor: `target/surefire-reports/index.html` · Başarısız testlerin ekran görüntüleri: `screenshots/`

### Test grupları

- `creates-data` – Gerçek kayıt oluşturan testler (kayıt olma, iletişim mesajı gönderme). Paylaşımlı test ortamını kirletmemek için
  `testng.xml` içinde hariç tutulur.
- Açıklamasında **"bilinen bug"** yazan testler kabul kriterine göre doğru davranışı doğrular ve ilgili JIRA bug kaydı açık olduğu
  sürece **FAIL** olması beklenir. Bug düzeltildiğinde test kendiliğinden geçer.

## Son koşum (15.10.2026, `mvn test -Dheadless=true`)

| Suite | Sonuç |
|---|---|
| TestNG UI | 48 test – 40 PASS, 8 FAIL (bilinen bug) |

Başarısız testlerin **tamamı** aşağıdaki JIRA bug kayıtlarına karşılık gelir; otomasyon kaynaklı hata yoktur.

## Testlerin doğruladığı bilinen bug'lar (JIRA – SCRUM projesi)

| Bug | Özet | Test |
|---|---|---|
| SCRUM-29 | Kayıt sonrası "Success" yerine "Registration successful! Please login." | US01 TC_01 (`creates-data`) |
| SCRUM-30 | Confirm Password alanı maskelenmiyor | US01 TC_19 |
| SCRUM-31 / 32 / 33 | Login butonu boşken aktif / "Required" / "Invalid email address" metinleri | US02 TC_02, TC_04, TC_05, TC_06 |
| SCRUM-35 | Para birimi $ / ₺ tutarsızlığı | US06 TC_13 |
| SCRUM-235 | İlave seçilince detay fiyatı güncellenmiyor | US07 TC_02 |
| SCRUM-236 | "." içermeyen email login'de kabul ediliyor | US02 TC_03 |
| SCRUM-237 | 25 karakterden uzun email iletişim formunda reddediliyor | US20 (`creates-data`) |

Tüm bulgular ve manuel koşum sonuçları için bitirme projesindeki Test Planı ve JIRA `SCRUM` projesine bakınız.

## Windows / Türkçe ortam notu

Kullanıcı klasöründe Türkçe karakter (ör. `ç`) varsa Selenium'un HTTP istemcisi `Unable to establish loopback connection` hatası verir.
`pom.xml` içindeki `unixdomain.tmpdir` (varsayılan `C:/Windows/Temp`) ile ASCII bir klasör verilir. IDE'den koşarken VM option olarak
`-Djdk.net.unixdomain.tmpdir=C:/Windows/Temp` ekleyin.
