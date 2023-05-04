package com.es.phoneshop.web;

import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.ArrayListProductDao;
import com.es.phoneshop.model.product.dao.ProductDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductDetailsPageServletTest {
    private final ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();
    private final long productId = 1L;
    private Product product;
    @Mock
    private ProductDao productDao = Mockito.mock(ArrayListProductDao.class);
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig serverConfig;

    @Before
    public void setup() throws ServletException {
        servlet.init(serverConfig);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        servlet.setProductDao(productDao);
        product = new Product();
        product.setId(productId);
    }

    @Test
    public void doGet_productDetailsPath_forward() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/" + productId);
        when(productDao.getProduct(productId)).thenReturn(product);

        servlet.doGet(request, response);

        verify(productDao).getProduct(productId);
        verify(request).setAttribute("product", product);
        verify(request).getRequestDispatcher(eq("/WEB-INF/pages/productDetails.jsp"));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void doGet_productPriceHistory_forwardToProductPriceHistory() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/" + productId + "/priceHistory");
        when(productDao.getProduct(productId)).thenReturn(product);

        servlet.doGet(request, response);

        verify(productDao).getProduct(productId);
        verify(request).setAttribute("product", product);
        verify(request).getRequestDispatcher(eq("/WEB-INF/pages/productPriceHistory.jsp"));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void doGet_invalidPathAfterProductIdAttribute_forwardToPageNotFound() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/" + productId + "/invalidPath");

        servlet.doGet(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND));
    }

    @Test(expected = ProductNotFoundException.class)
    public void doGet_nonexistentProductId_ProductNotFoundException() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/" + productId);
        when(productDao.getProduct(productId)).thenThrow(ProductNotFoundException.class);

        servlet.doGet(request, response);

        verify(productDao).getProduct(productId);
    }

}