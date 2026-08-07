package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ProductsPage;

public class LoginTest extends BaseTest {

    @Test(description = "Valid user can log in and land on the products page")
    public void validLoginShowsProducts() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(cfg("baseUrl"));

        ProductsPage productsPage = loginPage.loginAs(
                cfg("validUsername"), cfg("validPassword"));

        Assert.assertEquals(productsPage.getPageTitle(), "Products");
        Assert.assertTrue(productsPage.getProductNames().size() > 0,
                "Expected at least one product to be listed");
    }

    @Test(description = "Locked out user sees an error message")
    public void lockedOutUserSeesError() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(cfg("baseUrl"));

        loginPage.loginAs(cfg("invalidUsername"), cfg("invalidPassword"));

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Expected an error message to show");
        Assert.assertTrue(loginPage.getErrorMessage().contains("locked out"));
    }

    @Test(description = "User can add a product to the cart")
    public void addProductToCart() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(cfg("baseUrl"));

        ProductsPage productsPage = loginPage.loginAs(
                cfg("validUsername"), cfg("validPassword"));

        productsPage.addFirstProductToCart();
        Assert.assertEquals(productsPage.getCartCount(), "1");
    }
}
