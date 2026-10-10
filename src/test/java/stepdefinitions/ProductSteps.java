package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.ProductsPage;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ProductSteps {

    private ProductsPage productsPage;

    @Given("user is on the products page")
    public void userIsOnTheProductsPage() {
        productsPage = new ProductsPage().open();
    }

    @When("user selects the {string} category")
    public void selectCategory(String category) {
        productsPage.selectCategory(category);
    }

    @When("user searches for {string}")
    public void search(String text) {
        productsPage.search(text);
    }

    @Then("{int} products are listed")
    public void productsAreListed(int count) {
        assertEquals(productsPage.productNames().size(), count);
    }

    @Then("product {string} is listed")
    public void productIsListed(String name) {
        assertTrue(productsPage.productNames().contains(name), name + " listelenmeli");
    }

    @Then("only product {string} is listed")
    public void onlyProductIsListed(String name) {
        assertEquals(productsPage.productNames(), List.of(name));
    }

    @Then("all listed products belong to {string}")
    public void allBelongTo(String category) {
        List<String> tags = productsPage.cardCategoryTags();
        assertFalse(tags.isEmpty());
        assertTrue(tags.stream().allMatch(category::equals), "Kategori etiketleri: " + tags);
    }

    @Then("no product is listed")
    public void noProductIsListed() {
        assertTrue(productsPage.productNames().isEmpty());
    }

    @Then("page shows {string}")
    public void pageShows(String text) {
        assertTrue(productsPage.pageText().contains(text));
    }
}
