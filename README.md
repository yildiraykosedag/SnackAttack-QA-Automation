# Snack Attack – QA Test Automation

QA Bitirme Projesi – **Yıldıray Kösedağ**

Test edilen uygulama: <https://snackattack.deployedprojects.xyz/> · API: <https://snackattack-api.deployedprojects.xyz/swagger-ui/index.html>

| Ödev maddesi | İçerik | Konum |
|---|---|---|
| 13 | Selenium + TestNG – Page Object Model | `src/test/java/pages`, `src/test/java/tests/ui`, `testng-ui.xml` |
| 14 | Cucumber (BDD) + Selenium + POM | `src/test/resources/features`, `stepdefinitions`, `runners`, `hooks`, `testng-cucumber.xml` |
| 15 | Postman Collection + assertions | `postman/` |
| 16 | Rest Assured + POJO (Serialization / Deserialization) | `src/test/java/pojos`, `src/test/java/tests/api`, `testng-api.xml` |
| 17 | Database (PostgreSQL / JDBC) testleri | `src/test/java/tests/db`, `utilities/DBUtils.java` |
| 18 | End-to-End (UI + API + DB) testleri | `src/test/java/tests/e2e`, `testng-db-e2e.xml` |

## Teknolojiler

Java 17+ · Maven · Selenium 4 · TestNG 7 · Cucumber 7 · Rest Assured 5 · Jackson · PostgreSQL JDBC · Datafaker

## Proje yapısı

```
SnackAttack-QA-Automation
├── pom.xml
├── configuration.example.properties   # kopyalayıp configuration.properties yapın (git'e gönderilmez)
├── testng-all.xml / testng-ui.xml / testng-cucumber.xml / testng-api.xml / testng-db-e2e.xml
├── postman/
│   ├── SnackAttack_API.postman_collection.json
│   └── SnackAttack_Test.postman_environment.json
└── src/test
    ├── java
    │   ├── pages/            BasePage, HomePage, RegisterPage, LoginPage, ProductsPage, ProductDetailModal, CartPage, ContactPage
    │   ├── tests/ui/         US01_RegisterTest, US02_LoginTest, US06_ProductsTest, US07_ProductDetailTest, US20_ContactTest
    │   ├── stepdefinitions/  RegisterSteps, LoginSteps, ProductSteps, ContactSteps
    │   ├── runners/          CucumberRunner, FailedRunner
    │   ├── hooks/            Hooks
    │   ├── pojos/            ApiResponse<T>, Product, ProductPage, Category, AdditionCategory,
    │   │                     ContactMessageRequest/Response, LoginRequest, CartItemRequest
    │   ├── tests/api/        ProductApiTest, CategoryApiTest, ContactMessageApiTest, AuthAndCartApiTest
    │   ├── tests/db/         DatabaseTest
    │   ├── tests/e2e/        ProductConsistencyE2ETest, ContactMessageE2ETest
    │   └── utilities/        Driver, ConfigReader, ReusableMethods, TestDataFactory, ApiSpec, DBUtils
    └── resources/features/   US01_register, US02_login, US06_products, US20_contact
```

## Kurulum

1. JDK 17+, Maven 3.9+ ve Google Chrome kurulu olmalı (ChromeDriver Selenium Manager ile otomatik indirilir).
2. `configuration.example.properties` dosyasını `configuration.properties` olarak kopyalayın ve doldurun:
   - `user.email` / `user.password` – aktif bir User test hesabı (giriş gerektiren testler; boşsa bu testler **SKIP** olur)
   - `db.host`, `db.port`, `db.user`, `db.password` – eğitmenin verdiği salt okunur DB bilgileri
   - Değerler ortam değişkeni olarak da verilebilir: `DB_PASSWORD`, `USER_PASSWORD` …

## Koşum

```bash
mvn test                                   # tüm suite'ler (testng-all.xml)
mvn test -Dsuite=testng-ui.xml             # 13 – TestNG UI
mvn test -Dsuite=testng-cucumber.xml       # 14 – Cucumber
mvn test -Dsuite=testng-api.xml            # 16 – Rest Assured
mvn test -Dsuite=testng-db-e2e.xml         # 17-18 – DB + E2E
mvn test -Dsuite=testng-cucumber.xml -Dcucumber.filter.tags="@US06"
mvn test -Dsuite=testng-ui.xml -Dheadless=true
```

Raporlar: `target/surefire-reports/index.html` (TestNG), `target/cucumber-reports/cucumber.html` (Cucumber).
Başarısız UI testlerinin ekran görüntüleri `screenshots/` klasörüne kaydedilir.

Postman: koleksiyon ve environment dosyalarını Postman'e import edin, `userEmail` / `userPassword` değerlerini doldurup **Collection Runner** ile çalıştırın.

### Test grupları

- `creates-data` – Gerçek kayıt oluşturan testler (kayıt olma, iletişim mesajı gönderme, E2E mesaj akışı). Paylaşımlı test
  ortamını kirletmemek için UI / API suite'lerinde varsayılan olarak hariç tutulur; Cucumber'da `@creates-data` etiketi.
- Açıklamasında **"bilinen bug"** yazan testler, kabul kriterine göre doğru davranışı doğrular ve ilgili JIRA bug kaydı
  açık olduğu sürece **FAIL** olması beklenir. Bug düzeltildiğinde test kendiliğinden geçer.

## Son koşum (10.10.2026, `mvn test -Dheadless=true`)

| Suite | Sonuç |
|---|---|
| TestNG UI (madde 13) | 48 test – 40 PASS, 8 FAIL (bilinen bug) |
| Cucumber (madde 14) | 34 senaryo – 30 PASS, 4 FAIL (bilinen bug) |
| Rest Assured (madde 16) | 19 test – 14 PASS, 5 FAIL (bilinen bug) |
| DB + E2E (madde 17-18) | DB 12 test – 10 PASS, 2 FAIL (bilinen bug); E2E 4/4 PASS (+2 `creates-data` E2E ayrıca koşuldu, 2/2 PASS) |
| Postman / Newman (madde 15) | 20 istek, 47 assertion – 39 PASS, 8 FAIL (bilinen bug) |

Başarısız testlerin **tamamı** aşağıdaki JIRA bug kayıtlarına karşılık gelir; otomasyon kaynaklı hata yoktur.

## Bilinen bug'lar (JIRA – SCRUM projesi)

| Bug | Özet | Otomasyon |
|---|---|---|
| SCRUM-29 | Kayıt sonrası "Success" yerine "Registration successful! Please login." | US01 TC_01 |
| SCRUM-30 | Confirm Password alanı maskelenmiyor | US01 TC_19, US01 feature |
| SCRUM-31 / 32 / 33 | Login butonu boşken aktif / "Required" / "Invalid email address" metinleri | US02 |
| SCRUM-35 | Para birimi $ / ₺ tutarsızlığı | US06 TC_13 |
| SCRUM-36 | Misafir sepete ekleme bilgilendirmesi yok | US07 TC_08 |
| SCRUM-232 | UI'dan sipariş oluşturulamıyor ("Validation failed") – API ile `name` alanı eklenerek oluşturulabiliyor | Manuel (UI) |
| SCRUM-235 | İlave seçilince detay fiyatı güncellenmiyor | US07 TC_02 |
| SCRUM-236 | "." içermeyen email login'de kabul ediliyor | US02 TC_03 |
| SCRUM-237 | 25 karakterden uzun email iletişim formunda reddediliyor | US20, ContactMessageApiTest, Postman TC_API_11 |
| SCRUM-244 / 245 | `/category/admin`, `/products/allProduct-admin` yetkisiz erişime açık | CategoryApiTest, Postman TC_API_08 / 08b |
| SCRUM-246 | `allProduct-client` search olmadan 400 + NullPointerException | ProductApiTest, Postman TC_API_05 |
| SCRUM-247 | `populer-list` 500 Internal Server Error | ProductApiTest, Postman TC_API_06 |
| SCRUM-248 | `getAuthenticatedUser` yanıtında bcrypt şifre hash'i dönüyor | AuthAndCartApiTest, Postman TC_API_14 |
| SCRUM-249 | `createOrder` hata yanıtında SQL sorgusu sızıyor; customerName / customerEmail NULL kaydediliyor | DatabaseTest DB_12 |
| SCRUM-250 | Sipariş kaleminin fiyatı NULL / 0.0 kaydediliyor | DatabaseTest DB_11, Postman TC_API_19 |
| SCRUM-251 | Sipariş sonrası sepet temizlenmiyor | Manuel (API) |
| SCRUM-252 | Kullanıcı kendi siparişini `GET /api/orders/{id}` ile göremiyor (400 Access Denied) | Manuel (API) |
| SCRUM-253 | My Orders: durum filtresi yok, ödeme yöntemi / saat boş, TRY sipariş $ ile gösteriliyor | Manuel (UI) |
| SCRUM-257 | Admin ürün silme isteği yanlış sunucuya gidiyor (`DELETE /api/products/{id}` → 404) | Manuel (Admin UI) |
| SCRUM-258 | Kategori seçilmeden yeni ürün eklenebiliyor | Manuel (Admin UI) |
| SCRUM-259 | İndirim fiyattan büyük girilebiliyor; indirim fiyata yansımıyor | Manuel (Admin UI) |
| SCRUM-260 | Ürün Düzenleme formunda "Active" seçeneği yok | Manuel (Admin UI) |
| SCRUM-261 / 262 | İlave (addition) formunda Name/Description doğrulaması yok; Price/Category eksikse sessiz hata | Manuel (Admin UI) |
| SCRUM-263 / 268 / 269 | Support Requests: Email ve Time filtreleri hatalı, boş sonuçta bilgilendirme yok | Manuel (Admin UI) |
| SCRUM-264 / 267 | Order Management: CANCELED sipariş PENDING görünüyor, sipariş detayı yok | Manuel (Admin UI) |
| SCRUM-265 | Payment Management'ta ödemeler listelenmiyor | Manuel (Admin UI) |
| SCRUM-266 | Ürünü olan kategori silinirken 500, uyarı yok | Manuel (Admin UI) |
| SCRUM-270 | Admin kaydı sonrası "Success" yerine "Registration successful! You can now log in." | Manuel (Admin UI) |
| SCRUM-271 | Hatalı girişte ham "CredentialsSignin" kodu gösteriliyor (user + admin) | Manuel (UI) |
| SCRUM-272 | Ana admin (admin@example.com) silinmeye karşı korunmuyor | Manuel (Admin UI) |
| SCRUM-273 | Kullanıcı/admin silme ve kategori ekleme/silmede başarı mesajı yok | Manuel (Admin UI) |
| SCRUM-274 | Admin liste ekranlarında beklenen kolonlar eksik | Manuel (Admin UI) |
| SCRUM-275 | Admin ürün aramasında sonuç yokken bilgilendirme yok | Manuel (Admin UI) |

## Manuel koşum özeti (Xray Test Execution SCRUM-243)

192 test case'in tamamı koşuldu – **120 PASSED, 72 FAILED** (BLOCKED / NOT RUN yok). Admin paneli testleri 15.10.2026'da
`mehmet@admin.com` ile koşuldu; ortak ortamı korumak için oluşturma/silme senaryoları test verisi ("QA YK …", test admini /
test kullanıcısı, otomasyonun oluşturduğu iletişim mesajı) üzerinde yapıldı. Arayüzden sipariş verilemediği için (SCRUM-232)
sipariş statüsü senaryoları mevcut Order #2 üzerinden yürütüldü.

## Teslim belgeleri (`docs/`)

| Dosya | Ödev maddesi |
|---|---|
| `Snack_Attack_Test_Plani.pdf` / `.docx` | 1 – Test Planı (IEEE 829) |
| `Snack_Attack_Test_Cases_Yildiray_Kosedag.xlsx` | 2-5, 10 – Site / User Story analizi, 192 test case, koşum sonuçları, RTM |
| `Xray_Test_Import_SnackAttack.csv` | 8 – Xray Test Case Importer dosyası |

JIRA: `SCRUM` projesi – Story SCRUM-8…28, Test SCRUM-37…228, Test Plan SCRUM-242, Test Execution SCRUM-243, Bug SCRUM-29…36, 229…241, 244…275.

## Windows / Türkçe ortam notları

- **Türkçe locale (tr_TR):** Rest Assured içeride `"failure".toUpperCase()` kullanır; Türkçe locale'de bu `FAİLURE` olur ve 4xx/5xx
  yanıtlarda `HttpResponseException` fırlatılır. `pom.xml` surefire `argLine` ile `-Duser.language=en -Duser.country=US` verilir,
  `ApiSpec` de IDE koşumları için `Locale.US` ayarlar.
- **Kullanıcı klasöründe Türkçe karakter (ör. `ç`):** JDK, Selenium'un HTTP istemcisi için geçici klasörde Unix-domain soket açar;
  yol ASCII dışı karakter içerirse `Unable to establish loopback connection` hatası alınır. `pom.xml` içindeki
  `unixdomain.tmpdir` (varsayılan `C:/Windows/Temp`) ile ASCII bir klasör verilir. IDE'den koşarken VM option olarak
  `-Djdk.net.unixdomain.tmpdir=C:/Windows/Temp` ekleyin.
