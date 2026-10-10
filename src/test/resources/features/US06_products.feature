@US06 @ui
Feature: US_06 - User urunleri gorebilmeli, listeleyebilmeli ve aratabilmeli

  Background:
    Given user is on the products page

  @smoke
  Scenario: TC_01 - Tum urunler listelenir
    Then 10 products are listed
    And product "Et Döner 100g" is listed

  Scenario Outline: <tc> - "<category>" kategorisi yalnizca ilgili urunleri listeler
    When user selects the "<category>" category
    Then all listed products belong to "<category>"
    And product "<product>" is listed

    Examples:
      | tc    | category | product            |
      | TC_03 | Doner    | Et Döner 100g      |
      | TC_04 | Burger   | Klasik Burger 120g |
      | TC_05 | Pizza    | Pizza Margherita   |
      | TC_06 | Drinks   | Ayran 300ml        |
      | TC_07 | Desserts | Künefe             |
      | TC_08 | Salads   | Mevsim Salata      |

  Scenario: TC_10 - Urun adi ile arama yapilir
    When user searches for "Pizza Margherita"
    Then only product "Pizza Margherita" is listed

  @negative
  Scenario: TC_11 - Bulunmayan urun aratildiginda bilgilendirme yapilir
    When user searches for "xyzqwe123"
    Then no product is listed
    And page shows "Ürün bulunamadı."
