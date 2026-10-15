package tests.db;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import utilities.DBUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/** Veritabanı (PostgreSQL – snack_attack şeması) doğrulama testleri. Kişisel veri okunmaz, yalnızca sayım / yapı / referans bütünlüğü. */
public class DatabaseTest {

    @Test(description = "DB_01 – Bağlantı kurulur ve salt okunur kullanıcı ile çalışılır")
    public void connectionIsReadOnly() throws Exception {
        assertTrue(DBUtils.getConnection().isValid(10));
        Map<String, Object> row = DBUtils.queryOne("select current_user as usr, current_database() as db");
        assertEquals(row.get("usr"), "db_readonly");
        assertEquals(row.get("db"), "snack_attack_db");
    }

    @Test(description = "DB_02 – Beklenen tablolar mevcut")
    public void expectedTablesExist() {
        List<String> tables = DBUtils.query("select table_name from information_schema.tables where table_schema = 'snack_attack'")
                .stream().map(r -> (String) r.get("table_name")).collect(Collectors.toList());
        assertTrue(tables.containsAll(List.of("users", "user_roles", "product", "category_entity", "product_category",
                "carts", "cart_item", "orders", "order_items", "payments", "contact_message", "product_additions")), tables.toString());
    }

    // Ortak test ortamında admin testleri ürün/kategori ekleyebildiği için sabit sayı yerine başlangıç (seed) verisi doğrulanır.
    @Test(description = "DB_03 – US_06: başlangıç verisindeki 28 ürün mevcut, tüm ürünlerin fiyatı pozitif ve ismi dolu")
    public void productsAreValid() {
        assertTrue(DBUtils.count("select count(*) from product") >= 28L, "En az 28 başlangıç ürünü olmalı");
        assertEquals(DBUtils.count("select count(*) from product where price is null or price <= 0"), 0L);
        assertEquals(DBUtils.count("select count(*) from product where name is null or trim(name) = ''"), 0L);
    }

    @Test(description = "DB_04 – US_06: 6 başlangıç kategorisi aktif ve her ürün en az bir kategoriye bağlı "
            + "(bilinen bug SCRUM-258: admin kategori seçmeden ürün ekleyebiliyor)")
    public void categoriesAndRelations() {
        assertEquals(DBUtils.count("select count(*) from category_entity where active = true and name in "
                + "('DONER', 'HAMBURGER', 'PIZZA', 'ICECEKLER', 'TATLILAR', 'SALATALAR')"), 6L);
        assertEquals(DBUtils.count("select count(*) from product p where not exists "
                + "(select 1 from product_category pc where pc.product_id = p.id)"), 0L, "Kategorisiz ürün olmamalı");
    }

    @Test(description = "DB_05 – US_07: Klasik Burger 120g fiyatı 75 ve HAMBURGER kategorisinde")
    public void productDetailMatches() {
        Map<String, Object> row = DBUtils.queryOne("select p.price, c.name as category from product p "
                + "join product_category pc on pc.product_id = p.id join category_entity c on c.id = pc.category_id "
                + "where p.name = ?", "Klasik Burger 120g");
        assertNotNull(row);
        assertEquals(((Number) row.get("price")).doubleValue(), 75.0);
        assertEquals(row.get("category"), "HAMBURGER");
    }

    @Test(description = "DB_06 – Referans bütünlüğü: sepet kalemleri geçerli ürün ve sepetlere bağlı")
    public void cartItemsReferenceIntegrity() {
        assertEquals(DBUtils.count("select count(*) from cart_item ci left join product p on p.id = ci.product_id where p.id is null"), 0L);
        assertEquals(DBUtils.count("select count(*) from cart_item ci left join carts c on c.id = ci.cart_id where c.id is null"), 0L);
        assertEquals(DBUtils.count("select count(*) from cart_item where quantity < 1"), 0L, "Miktar 1'den küçük olmamalı");
    }

    @Test(description = "DB_07 – US_11: admin@example.com ana admin hesabı mevcut ve ADMIN rolünde")
    public void builtInAdminExists() {
        Map<String, Object> row = DBUtils.queryOne("select r.role_type from users u join user_roles r on r.id = u.user_role_id "
                + "where u.email = ?", "admin@example.com");
        assertNotNull(row, "admin@example.com mevcut olmalı");
        assertEquals(row.get("role_type"), "ADMIN");
    }

    @Test(description = "DB_08 – Kullanıcı şifreleri düz metin değil, hash olarak saklanır")
    public void passwordsAreHashed() {
        assertEquals(DBUtils.count("select count(*) from users where password is not null and password !~ '^\\$2[aby]\\$'"), 0L,
                "Tüm şifreler BCrypt hash olmalı");
    }

    @Test(description = "DB_09 – US_20: İletişim mesajlarında zorunlu alanlar dolu ve email ≤ 25 karakter")
    public void contactMessagesAreValid() {
        assertTrue(DBUtils.count("select count(*) from contact_message") > 0);
        assertEquals(DBUtils.count("select count(*) from contact_message where name is null or email is null "
                + "or subject is null or message is null"), 0L);
        assertEquals(DBUtils.count("select count(*) from contact_message where length(email) > 25"), 0L);
    }

    @Test(description = "DB_10 – Her siparişin en az bir kalemi, geçerli ödeme yöntemi ve pozitif toplamı olmalı")
    public void ordersHaveItemsAndValidPayment() {
        long orphanOrders = DBUtils.count("select count(*) from orders o where not exists (select 1 from order_items i where i.order_id = o.id)");
        long invalidPayment = DBUtils.count("select count(*) from orders where payment_method not in ('CREDIT_CARD','CASH','PAYPAL','BANK_TRANSFER') or total_price <= 0");
        assertEquals(orphanOrders, 0L, "Kalemi olmayan sipariş bulunmamalı");
        assertEquals(invalidPayment, 0L, "Ödeme yöntemi / toplam tutar geçersiz sipariş bulunmamalı");
    }

    @Test(description = "DB_11 – Bilinen bug SCRUM-250: sipariş kaleminin fiyatı NULL / 0.0 kaydediliyor")
    public void orderItemsHavePositivePrice() {
        long badItems = DBUtils.count("select count(*) from order_items where price is null or total_price is null or total_price <= 0");
        assertEquals(badItems, 0L, "Tüm sipariş kalemlerinin birim ve toplam fiyatı pozitif olmalı");
    }

    @Test(description = "DB_12 – Bilinen bug SCRUM-249: siparişte müşteri adı / email kaydedilmiyor (NULL)")
    public void ordersStoreCustomerInfo() {
        long missing = DBUtils.count("select count(*) from orders where customer_name is null or customer_email is null");
        assertEquals(missing, 0L, "createOrder ile gönderilen customerName / customerEmail kaydedilmeli");
    }

    @AfterClass(alwaysRun = true)
    public void close() {
        DBUtils.close();
    }
}
