package com.es.phoneshop.web.servlet;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.exception.product.ProductNotFoundException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.RecentProducts;
import com.es.phoneshop.service.RecentProductsService;
import com.es.phoneshop.service.impl.DefaultRecentProductsService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Deque;
import java.util.UUID;

import static com.es.phoneshop.web.constant.ServletConstant.JspPage;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductPriceHistoryPageServletTest {
    private final ProductPriceHistoryPageServlet servlet = new ProductPriceHistoryPageServlet();
    private final UUID productId = UUID.randomUUID();
    private final Product product = new Product();
    @Mock
    private Deque<Product> products;
    @Mock
    private RecentProducts recentProducts = Mockito.mock(RecentProducts.class);
    @Mock
    private ProductDao productDao = Mockito.mock(ArrayListProductDao.class);
    @Mock
    private RecentProductsService recentProductsService = Mockito.mock(DefaultRecentProductsService.class);
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig serverConfig;
    @Mock
    private HttpSession httpSession;

    @Before
    public void setup() throws ServletException, NoSuchFieldException, IllegalAccessException {
        servlet.init(serverConfig);
        setProductDao();
        setRecentProductsService();
        product.setId(productId);

        when(request.getSession()).thenReturn(httpSession);
        when(recentProducts.getProducts()).thenReturn(products);
        when(recentProductsService.getRecentProducts(httpSession)).thenReturn(recentProducts);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void doGet_productId_forwardToProductPriceHistory() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/" + productId);
        when(productDao.getItem(productId)).thenReturn(product);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq(RequestAttribute.PRODUCT), eq(product));
        verify(request).setAttribute(eq(RequestAttribute.RECENT_PRODUCTS), eq(products));
        verify(request).getRequestDispatcher(eq(JspPage.PRODUCT_PRICE_HISTORY));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void doGet_noProductIdInRequestPath_pageNotFoundError() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");

        servlet.doGet(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND));
    }

    @Test(expected = ProductNotFoundException.class)
    public void doGet_nonexistentProductId_ProductNotFoundException() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/" + productId);
        when(productDao.getItem(productId)).thenThrow(ProductNotFoundException.class);

        servlet.doGet(request, response);
    }

    private void setProductDao() throws IllegalAccessException, NoSuchFieldException {
        Field productDaoField = ProductPriceHistoryPageServlet.class.getDeclaredField("productDao");
        productDaoField.setAccessible(true);
        productDaoField.set(servlet, productDao);
    }

    private void setRecentProductsService() throws IllegalAccessException, NoSuchFieldException {
        Field recentProductsServiceField = ProductPriceHistoryPageServlet.class
                .getDeclaredField("recentProductsService");
        recentProductsServiceField.setAccessible(true);
        recentProductsServiceField.set(servlet, recentProductsService);
    }

}
