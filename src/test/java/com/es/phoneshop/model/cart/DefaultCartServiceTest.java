package com.es.phoneshop.model.cart;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.service.CartService;
import com.es.phoneshop.model.cart.service.DefaultCartService;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.ProductDao;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCartServiceTest {
    private static final Product product = new Product();
    private static final Long productId = 1L;
    private static Cart cart;
    private static CartService cartService;
    @Mock
    private static HttpSession session;
    @Mock
    private static ProductDao productDao;

    @Before
    public void init() throws NoSuchFieldException, IllegalAccessException {
        cart = new Cart();
        product.setId(productId);
        product.setStock(2);
        product.setPrice(new BigDecimal(1));

        cartService = DefaultCartService.getInstance();
        setProductDao();

        when(session.getId()).thenReturn("sessionId");
        when(productDao.getProduct(any())).thenReturn(product);
    }

    @Test
    public void getCart_nullCart_notNullCart() {
        cart = null;
        when(session.getAttribute(any())).thenReturn(cart);

        Cart newCart = cartService.getCart(session);

        verify(session).setAttribute(any(), eq(newCart));
        assertNotNull(newCart);
    }

    @Test
    public void add_noProductInCart_addProductToCart() throws OutOfStockException {
        int quantity = 1;

        cartService.add(cart, productId, quantity);

        assertFalse(cart.getItems().isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals(product.getPrice(), cart.getTotalCost());
        assertEquals(quantity, cart.getTotalQuantity());
    }

    @Test
    public void add_productInCart_changeProductQuantityInCart() throws OutOfStockException {
        int quantity = 1;
        int newQuantity = product.getStock();

        cartService.add(cart, productId, quantity);
        cartService.add(cart, productId, quantity);

        assertFalse(cart.getItems().isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals(newQuantity, cart.getItems().get(0).getQuantity());
        assertEquals(product.getPrice().multiply(new BigDecimal(newQuantity)), cart.getTotalCost());
        assertEquals(newQuantity, cart.getTotalQuantity());
    }

    @Test(expected = OutOfStockException.class)
    public void add_productQuantityMoreThenStock_OutOfStockException() throws OutOfStockException {
        int quantity = product.getStock() + 1;

        cartService.add(cart, productId, quantity);
    }

    @Test
    public void delete_productInCart_deleteProductFromCart() throws OutOfStockException {
        int quantity = product.getStock();

        cartService.add(cart, productId, quantity);
        cartService.delete(cart, productId);

        assertEquals(0, cart.getTotalCost().intValue());
        assertEquals(0, cart.getTotalQuantity());
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    public void update_productInCart_updateProductInCart() throws OutOfStockException {
        int newQuantity = 1;

        cartService.add(cart, productId, product.getStock());
        cartService.update(cart, productId, newQuantity);

        assertFalse(cart.getItems().isEmpty());
        assertEquals(newQuantity, cart.getItems().get(0).getQuantity());
        assertEquals(product.getPrice().multiply(new BigDecimal(newQuantity)), cart.getTotalCost());
        assertEquals(newQuantity, cart.getTotalQuantity());
    }

    @Test(expected = OutOfStockException.class)
    public void update_productQuantityMoreThenStock_OutOfStockException() throws OutOfStockException {
        int quantity = product.getStock() + 1;

        cartService.add(cart, productId, product.getStock());
        cartService.update(cart, productId, quantity);
    }

    private void setProductDao() throws IllegalAccessException, NoSuchFieldException {
        Field productDaoField = DefaultCartService.class.getDeclaredField("productDao");
        productDaoField.setAccessible(true);
        productDaoField.set(cartService, productDao);
    }

}
