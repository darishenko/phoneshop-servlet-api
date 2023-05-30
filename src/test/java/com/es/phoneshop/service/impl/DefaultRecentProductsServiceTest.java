package com.es.phoneshop.service.impl;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.RecentProducts;
import com.es.phoneshop.service.impl.DefaultRecentProductsService;
import com.es.phoneshop.service.RecentProductsService;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultRecentProductsServiceTest {
    private static final UUID productId = UUID.randomUUID();
    private static RecentProductsService recentProductsService;
    private static RecentProducts recentProducts;
    private static final Product product = new Product("sgs", "Samsung Galaxy S",
            new BigDecimal(100), Currency.getInstance("USD"), 100, "SamsungS.jpg");

    @Mock
    private static ProductDao productDao;
    @Mock
    private static HttpSession session;

    @Before
    public void init() throws NoSuchFieldException, IllegalAccessException {
        product.setId(productId);
        recentProducts = new RecentProducts();
        recentProductsService = DefaultRecentProductsService.getInstance();
        setProductDao();
    }

    @Test
    public void getRecentProducts_nullRecentProducts_getRecentProducts() {
        recentProducts = null;
        when(session.getId()).thenReturn("sessionId");
        when(session.getAttribute(anyString())).thenReturn(recentProducts);

        recentProducts = recentProductsService.getRecentProducts(session);

        verify(session).setAttribute(any(), eq(recentProducts));
        assertNotNull(recentProducts);
    }

    @Test
    public void addToRecentProducts_nonexistentProductInRecent_addProductInRecentProducts()
            throws NoSuchFieldException, IllegalAccessException {
        int maxRecentProductCount = getRecentProductsMaxCount();
        when(productDao.getItem(productId)).thenReturn(product);

        recentProductsService.addToRecentProducts(recentProducts, productId);

        assertEquals(recentProducts.getProducts().getLast(), product);
        assertTrue(maxRecentProductCount >= recentProducts.getProducts().size());
    }

    @Test
    public void addToRecentProducts_existingProductInRecent_addProductInRecentProductsAsLast()
            throws NoSuchFieldException, IllegalAccessException {
        recentProducts.getProducts().add(product);
        recentProducts.getProducts().add(new Product());
        int maxRecentProductCount = getRecentProductsMaxCount();
        int recentProductsCount = recentProducts.getProducts().size();
        when(productDao.getItem(productId)).thenReturn(product);

        recentProductsService.addToRecentProducts(recentProducts, productId);

        assertEquals(recentProducts.getProducts().getLast(), product);
        assertEquals(recentProductsCount, recentProducts.getProducts().size());
        assertTrue(maxRecentProductCount >= recentProducts.getProducts().size());
    }

    @Test
    public void addToRecentProducts_maxRecentProductsCount_deleteFirstProductFromRecentProducts()
            throws NoSuchFieldException, IllegalAccessException {
        recentProducts.getProducts().add(new Product());
        recentProducts.getProducts().add(new Product());
        recentProducts.getProducts().add(new Product());
        int recentProductsCount = recentProducts.getProducts().size();
        int maxRecentProductCount = getRecentProductsMaxCount();
        when(productDao.getItem(productId)).thenReturn(product);

        recentProductsService.addToRecentProducts(recentProducts, product.getId());

        assertEquals(recentProducts.getProducts().getLast(), product);
        assertEquals(recentProductsCount, recentProducts.getProducts().size());
        assertTrue(maxRecentProductCount >= recentProducts.getProducts().size());
    }

    private void setProductDao() throws IllegalAccessException, NoSuchFieldException {
        Field productDaoField = DefaultRecentProductsService.class.getDeclaredField("productDao");
        productDaoField.setAccessible(true);
        productDaoField.set(recentProductsService, productDao);
    }

    private int getRecentProductsMaxCount() throws NoSuchFieldException, IllegalAccessException {
        Field field = DefaultRecentProductsService.class.getDeclaredField("RECENT_PRODUCTS_MAX_COUNT");
        field.setAccessible(true);
        return (int) field.get(recentProductsService);
    }

}
