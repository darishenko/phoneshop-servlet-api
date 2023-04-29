package com.es.phoneshop.model.product;

import com.es.phoneshop.model.product.dao.ArrayListProductDao;
import com.es.phoneshop.model.product.dao.ProductDao;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.*;

public class ArrayListProductDaoTest {
    private ProductDao productDao;

    @Before
    public void setup() {
        productDao = ArrayListProductDao.getInstance();
    }

    @Test
    public void testFindProductsNoResults() {
        assertFalse(productDao.findProducts().isEmpty());
    }

    @Test
    public void testSaveNewProduct() throws ProductException {
        Currency usd = Currency.getInstance("USD");
        String productCode = "test-save-new-product";
        Product product = new Product(productCode, "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);
        long productId = product.getId();
        assertTrue(product.getId() > 0);
        Product resultProduct = productDao.getProduct(product.getId());
        assertNotNull(resultProduct);
        assertEquals(product, productDao.getProduct(productId));
    }

    @Test
    public void testSaveExistingProduct() throws ProductException {
        Currency usd = Currency.getInstance("USD");
        String productCodeBeforeEditing = "test-save-existing-product";
        String productCodeAfterEditing = "test-save-edited-product";
        Product product = new Product(productCodeBeforeEditing, "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);
        long productId = product.getId();
        product.setCode(productCodeAfterEditing);
        productDao.save(product);
        assertEquals(product, productDao.getProduct(productId));
    }

    @Test(expected = ProductException.class)
    public void testSaveCopyProductAsNew() throws ProductException {
        Currency usd = Currency.getInstance("USD");
        String productCodeBeforeEditing = "test-save-copy-product-as-new";
        Product product = new Product(productCodeBeforeEditing, "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);
        Product copyProduct = new Product(productCodeBeforeEditing, "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(copyProduct);
    }

    @Test(expected = ProductException.class)
    public void testSaveUndefinedProduct() throws ProductException {
        productDao.save(null);
    }

    @Test
    public void testFindProductWithZeroStockLevel() throws ProductException {
        Currency usd = Currency.getInstance("USD");
        String productCode = "test-find-product-with-zero-stock-level";
        Product product = new Product(productCode, "Samsung Galaxy S", new BigDecimal(100), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);
        List<Product> products = productDao.findProducts();
        assertFalse(products.contains(product));
    }

    @Test
    public void testFindProductWithNullPrice() throws ProductException {
        Currency usd = Currency.getInstance("USD");
        String productCode = "test-find-product-with-null-price";
        Product product = new Product(productCode, "Samsung Galaxy S", null, usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);
        List<Product> products = productDao.findProducts();
        assertFalse(products.contains(product));
    }

    @Test(expected = ProductException.class)
    public void testDeleteProduct() throws ProductException {
        Currency usd = Currency.getInstance("USD");
        String productCode = "test-delete-product";
        Product product = new Product(productCode, "Samsung Galaxy S", null, usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);
        long productId = product.getId();
        productDao.delete(productId);
        productDao.getProduct(productId);
    }

    @Test(expected = ProductException.class)
    public void testDeleteNonexistentProduct() throws ProductException {
        productDao.delete(0L);
    }

}
