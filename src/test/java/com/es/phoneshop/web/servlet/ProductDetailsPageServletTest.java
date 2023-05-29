package com.es.phoneshop.web.servlet;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.service.CartService;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.RecentProducts;
import com.es.phoneshop.model.product.dao.ArrayListProductDao;
import com.es.phoneshop.model.product.dao.ProductDao;
import com.es.phoneshop.model.product.service.DefaultRecentProductsService;
import com.es.phoneshop.model.product.service.RecentProductsService;
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
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductDetailsPageServletTest {
    private static final String PRODUCT_DETAILS_JSP = "/WEB-INF/pages/productDetails.jsp";

    private static final String CART_REQUEST_ATTRIBUTE = "cart";
    private static final String PRODUCT_REQUEST_ATTRIBUTE = "product";
    private static final String QUANTITY_REQUEST_ATTRIBUTE = "quantity";
    private static final String ADD_TO_CART_ERROR_REQUEST_ATTRIBUTE = "addToCartError";

    private final Long productId = 1L;
    private final Product product = new Product();
    private final ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();
    @Mock
    private Cart cart;
    @Mock
    private Deque<Product> products;
    @Mock
    private ProductDao productDao = Mockito.mock(ArrayListProductDao.class);
    @Mock
    private RecentProducts recentProducts = Mockito.mock(RecentProducts.class);
    @Mock
    private RecentProductsService recentProductsService = Mockito.mock(DefaultRecentProductsService.class);
    @Mock
    private CartService cartService;
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
        setCartService();
        setRecentProductsService();
        setProductDao();

        product.setId(productId);

        when(request.getSession()).thenReturn(httpSession);
        when(recentProducts.getProducts()).thenReturn(products);
        when(recentProductsService.getRecentProducts(httpSession)).thenReturn(recentProducts);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void doGet_productDetailsPath_forward() throws ServletException, IOException {
        setProductIdInRequestPath();
        when(productDao.getProduct(productId)).thenReturn(product);
        when(cartService.getCart(httpSession)).thenReturn(cart);

        servlet.doGet(request, response);

        verify(productDao).getProduct(productId);
        verify(request).setAttribute(CART_REQUEST_ATTRIBUTE, cart);
        verify(request).setAttribute(PRODUCT_REQUEST_ATTRIBUTE, product);
        verify(request).getRequestDispatcher(eq(PRODUCT_DETAILS_JSP));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void doGet_noProductIdInRequestPath_forwardToPageNotFound() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/");

        servlet.doGet(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND));
    }

    @Test(expected = ProductNotFoundException.class)
    public void doGet_nonexistentProductId_ProductNotFoundException() throws ServletException, IOException {
        setProductIdInRequestPath();
        when(productDao.getProduct(productId)).thenThrow(ProductNotFoundException.class);

        servlet.doGet(request, response);

        verify(productDao).getProduct(productId);
    }

    @Test
    public void doPost_quantityNotNumber_setAddToCartErrorAttribute() throws ServletException, IOException {
        setLocale();
        setProductIdInRequestPath();
        when(request.getParameter(QUANTITY_REQUEST_ATTRIBUTE)).thenReturn(QUANTITY_REQUEST_ATTRIBUTE);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq(ADD_TO_CART_ERROR_REQUEST_ATTRIBUTE), anyString());
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void doPost_quantityNotIntegerNumber_setAddToCartErrorAttribute() throws ServletException, IOException {
        setLocale();
        setProductIdInRequestPath();
        when(request.getParameter(QUANTITY_REQUEST_ATTRIBUTE)).thenReturn("1,1");

        servlet.doPost(request, response);

        verify(request).setAttribute(eq(ADD_TO_CART_ERROR_REQUEST_ATTRIBUTE), anyString());
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void doPost_productStockMoreThenQuantity_redirectWithSuccessMessage() throws ServletException, IOException {
        String redirectPath = String.format("%s/products/%d?message=%s", request.getContextPath(), productId,
                "Product was added to cart");
        setLocale();
        setProductIdInRequestPath();
        when(request.getParameter(QUANTITY_REQUEST_ATTRIBUTE)).thenReturn("1");

        servlet.doPost(request, response);

        verify(response).sendRedirect(redirectPath);
    }

    @Test
    public void doPost_addToCartQuantityMoreThenAvailable_outOfStockException() throws OutOfStockException, ServletException, IOException {
        setLocale();
        setProductIdInRequestPath();
        when(request.getParameter(QUANTITY_REQUEST_ATTRIBUTE)).thenReturn("1");
        when(cartService.getCart(httpSession)).thenReturn(cart);
        doThrow(OutOfStockException.class).when(cartService).add(cart, productId, 1);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq(ADD_TO_CART_ERROR_REQUEST_ATTRIBUTE), anyString());
        verify(response, never()).sendRedirect(anyString());
    }

    private void setProductIdInRequestPath(){
        when(request.getPathInfo()).thenReturn("/" + productId);
    }

    private void setLocale(){
        when(request.getLocale()).thenReturn(new Locale("ru"));
    }

    private void setCartService() throws IllegalAccessException, NoSuchFieldException {
        Field cartServiceField = ProductDetailsPageServlet.class.getDeclaredField("cartService");
        cartServiceField.setAccessible(true);
        cartServiceField.set(servlet, cartService);
    }

    private void setRecentProductsService() throws IllegalAccessException, NoSuchFieldException {
        Field recentProductsServiceField = ProductDetailsPageServlet.class
                .getDeclaredField("recentProductsService");
        recentProductsServiceField.setAccessible(true);
        recentProductsServiceField.set(servlet, recentProductsService);
    }

    private void setProductDao() throws IllegalAccessException, NoSuchFieldException {
        Field productDaoField = ProductDetailsPageServlet.class.getDeclaredField("productDao");
        productDaoField.setAccessible(true);
        productDaoField.set(servlet, productDao);
    }

}