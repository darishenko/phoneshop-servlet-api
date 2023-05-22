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
        product.setId(productId);
        product.setStock(2);
        cart = new Cart();
        cartService = DefaultCartService.getInstance();
        when(session.getId()).thenReturn("sessionId");
        when(productDao.getProduct(any())).thenReturn(product);
        setProductDao();
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
        cart = new Cart();
        int quantity = 1;

        cartService.add(cart, productId, quantity);

        assertFalse(cart.getItems().isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals(quantity, cart.getItems().get(0).getQuantity());
    }

    @Test
    public void add_productInCart_changeProductQuantityInCart() throws OutOfStockException {
        cart = new Cart();
        int quantity = 1;

        cartService.add(cart, productId, quantity);
        cartService.add(cart, productId, quantity);

        assertFalse(cart.getItems().isEmpty());
        assertEquals(1, cart.getItems().size());
        assertEquals(2 * quantity, cart.getItems().get(0).getQuantity());
    }

    @Test(expected = OutOfStockException.class)
    public void add_productQuantityMoreThenStock_OutOfStockException() throws OutOfStockException {
        cart = new Cart();
        int quantity = product.getStock() + 1;

        cartService.add(cart, productId, quantity);
    }

    private void setProductDao() throws IllegalAccessException, NoSuchFieldException {
        Field productDaoField = DefaultCartService.class.getDeclaredField("productDao");
        productDaoField.setAccessible(true);
        productDaoField.set(cartService, productDao);
    }
}
