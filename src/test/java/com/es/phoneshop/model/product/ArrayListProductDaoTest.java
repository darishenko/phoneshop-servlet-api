package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.DuplicateProductException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.product.dao.ArrayListProductDao;
import com.es.phoneshop.model.product.dao.ProductDao;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.*;

public class ArrayListProductDaoTest {
    private static ProductDao productDao;
    private static Currency currency;
    private static Product product1;
    private static Product product2;

    @BeforeClass
    public static void addTestProducts() {
        currency = Currency.getInstance("USD");
        product1 = new Product("test", "Samsung Galaxy S", new BigDecimal(100), currency, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        product2 = new Product("test", "Samsung Galaxy S II", new BigDecimal(50), currency, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao = ArrayListProductDao.getInstance();
        productDao.save(product1);
    }

    @Before
    public void setup() {
    }

    @Test
    public void getProduct_existingProductId_returnProduct() {
        Product currentProduct = productDao.findProducts("", null, null).get(0);
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
    public void findProduct_returnProductList() {
        List<Product> products;

        products = productDao.findProducts("", null, null);

        assertNotNull(products);
    }

    @Test
    public void findProduct_queryWordsFromProductDescription_returnProductList() {
        String queryWords = productDao.findProducts("", null, null).get(0).getDescription();
        List<Product> products;

        products = productDao.findProducts(queryWords, null, null);

        assertTrue(products.contains(product1));
    }

    @Test
    public void findProduct_priceAscOrder_returnSortedProductListByPrice() {
        Product product = new Product("priceAscOrder", "Palm Pixi", new BigDecimal(1), currency, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg");
        productDao.save(product);

        List<Product> products = productDao.findProducts("", SortField.price, SortOrder.asc);

        assertTrue(products.get(1).getPrice().compareTo(products.get(0).getPrice()) >= 0);
    }

    @Test
    public void findProduct_descriptionDescOrder_returnSortedProductListByDescription() {
        Product product = new Product("descriptionDescOrder", "Palm Pixi", new BigDecimal(1), currency, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg");
        productDao.save(product);

        List<Product> products = productDao.findProducts("", SortField.description, SortOrder.desc);

        assertTrue(products.get(1).getDescription().compareTo(products.get(0).getDescription()) < 0);
    }

    @Test
    public void save_newProduct_addProductToList() {
        int productCount = productDao.findProducts("", null, null).size() + 1;

        productDao.save(product2);
        List<Product> products = productDao.findProducts("", null, null);

        assertNotNull(product2.getId());
        assertEquals(productCount, products.size());
        assertTrue(products.contains(product2));
    }

    @Test
    public void save_modifiedProduct_modifyProductInList() {
        product2.setPrice(new BigDecimal(1));
        product2.setStock(1);
        productDao.save(product2);
        Product product = productDao.findProducts("", null, null).get(0);
        long productId = product.getId();
        product.setCode("edit");

        productDao.save(product);
        Product findProduct = productDao.getProduct(productId);

        assertNotNull(findProduct);
        assertEquals(product, findProduct);
    }

    @Test(expected = DuplicateProductException.class)
    public void save_copyOfExistingProduct_AddExistingProductException() {
        Product product = productDao.findProducts("", null, null).get(0);
        Product newProduct = new Product();
        ProductMapper.updateProduct(newProduct, product);

        productDao.save(newProduct);
    }

    @Test(expected = ProductNotFoundException.class)
    public void save_nonexistentProductId_ProductNotFoundException() {
        Product product = new Product();
        product.setId(0L);

        productDao.save(product);
    }

    @Test
    public void findProduct_zeroStockLevel_notFindProductWithZeroStockLevel() {
        Product currentProduct = productDao.findProducts("", null, null).get(0);
        currentProduct.setStock(0);

        List<Product> products = productDao.findProducts("", null, null);

        assertFalse(products.contains(currentProduct));
    }

    @Test
    public void findProduct_nullPrice_notFindProductWithNullPrice() {
        Product currentProduct = productDao.findProducts("", null, null).get(0);
        currentProduct.setPrice(null);

        List<Product> products = productDao.findProducts("", null, null);

        assertFalse(products.contains(currentProduct));
    }

    @Test
    public void delete_existingProduct_deleteProductFromList() {
        Product currentProduct = productDao.findProducts("", null, null).get(0);
        long productId = currentProduct.getId();

        productDao.delete(productId);
        List<Product> products = productDao.findProducts("", null, null);

        assertFalse(products.contains(currentProduct));
    }

}
