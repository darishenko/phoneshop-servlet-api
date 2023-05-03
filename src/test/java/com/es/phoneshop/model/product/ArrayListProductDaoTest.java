package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.DuplicateProductException;
import com.es.phoneshop.exception.ProductNotFoundException;
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
    private Currency currency;
    private Product product;

    @Before
    public void setup() {
        productDao = ArrayListProductDao.getInstance();
        currency = Currency.getInstance("USD");
        product = new Product("test", "Samsung Galaxy S", new BigDecimal(100), currency, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
    }

    @Test
    public void getProduct_existingProductId_returnProduct() {
        Product currentProduct = productDao.findProducts().get(0);
        long productId = currentProduct.getId();

        Product findProduct = productDao.getProduct(productId);

        assertNotNull(currentProduct);
        assertEquals(currentProduct, findProduct);
    }

    @Test(expected = ProductNotFoundException.class)
    public void getProduct_nonexistentProductId_ProductNotFoundException() {
        productDao.getProduct(0L);
    }

    @Test
    public void findProduct_zeroStockLevel_notFindProductWithZeroStockLevel() {
        Product currentProduct = productDao.findProducts().get(0);
        currentProduct.setStock(0);

        List<Product> products = productDao.findProducts();

        assertFalse(products.contains(currentProduct));
    }

    @Test
    public void findProduct_nullPrice_notFindProductWithNullPrice() {
        Product currentProduct = productDao.findProducts().get(0);
        currentProduct.setPrice(null);

        List<Product> products = productDao.findProducts();

        assertFalse(products.contains(currentProduct));
    }

    @Test
    public void findProduct_returnProductList() {
        List<Product> products;

        products = productDao.findProducts();

        assertNotNull(products);
        assertFalse(products.isEmpty());
    }

    @Test
    public void delete_existingProduct_deleteProductFromList() {
        Product currentProduct = productDao.findProducts().get(0);
        long productId = currentProduct.getId();

        productDao.delete(productId);
        List<Product> products = productDao.findProducts();

        assertFalse(products.contains(currentProduct));
    }

    @Test
    public void save_newProduct_addProductToList() {
        int productCount = productDao.findProducts().size() + 1;

        productDao.save(product);
        List<Product> products = productDao.findProducts();

        assertNotNull(product.getId());
        assertEquals(productCount, products.size());
        assertTrue(products.contains(product));
    }

    @Test
    public void save_modifiedProduct_modifyProductInList() {
        Product product = productDao.findProducts().get(0);
        long productId = product.getId();
        product.setCode("edit");

        productDao.save(product);
        Product findProduct = productDao.getProduct(productId);

        assertNotNull(findProduct);
        assertEquals(product, findProduct);
    }

    @Test (expected = DuplicateProductException.class)
    public void save_copyOfExistingProduct_AddExistingProductException() {
        Product product = productDao.findProducts().get(0);
        Product newProduct = new Product();
        ProductMapper.updateProduct(newProduct, product);

        productDao.save(newProduct);
    }

    @Test(expected = ProductNotFoundException.class)
    public void save_nonexistentProductId_ProductNotFoundException() {
        product.setId(0L);

        productDao.save(product);
    }

}
