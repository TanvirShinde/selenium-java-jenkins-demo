package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

public class ProductsPage extends BasePage {

    private final By pageTitle = By.className("title");
    private final By inventoryItems = By.className("inventory_item_name");
    private final By addToCartButtons = By.cssSelector("button[id^='add-to-cart']");
    private final By cartBadge = By.className("shopping_cart_badge");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    public String getPageTitle() {
        return textOf(pageTitle);
    }

    public List<String> getProductNames() {
        return driver.findElements(inventoryItems)
                .stream()
                .map(el -> el.getText())
                .collect(Collectors.toList());
    }

    public void addFirstProductToCart() {
        driver.findElements(addToCartButtons).get(0).click();
    }

    public String getCartCount() {
        return textOf(cartBadge);
    }
}
