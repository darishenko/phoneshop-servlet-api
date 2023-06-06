package com.es.phoneshop.web.servlet;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.enam.sort.SortField;
import com.es.phoneshop.enam.sort.SortOrder;
import com.es.phoneshop.exception.product.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.RecentProducts;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.RecentProductsService;
import com.es.phoneshop.service.impl.DefaultCartService;
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
import java.util.Locale;
import java.util.UUID;

import static com.es.phoneshop.web.constant.ServletConstant.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductListPageServletTest {
    private final ProductListPageServlet servlet = new ProductListPageServlet();
    private final UUID productId = UUID.randomUUID();
    @Mock
    private Deque<Product> products;
    @Mock
    private Cart cart;
    @Mock
    private RecentProducts recentProducts = Mockito.mock(RecentProducts.class);
    @Mock
    private ProductDao productDao = Mockito.mock(ArrayListProductDao.class);
    @Mock
    private CartService cartService = Mockito.mock(DefaultCartService.class);
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
        setCartService();
        setRecentProductsService();

        when(request.getSession()).thenReturn(httpSession);
        when(recentProducts.getProducts()).thenReturn(products);
        when(recentProductsService.getRecentProducts(httpSession)).thenReturn(recentProducts);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void doGet_setProductAttribute() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq(RequestAttribute.PRODUCTS), any());
        verify(request).getRequestDispatcher(eq(JspPage.PRODUCT_LIST));
    }

    @Test
    public void doGet_getQuerySortOrder_executeFindProducts() throws ServletException, IOException {
        when(request.getParameter(RequestParameter.QUERY)).thenReturn("query");
        when(request.getParameter(RequestParameter.SORT)).thenReturn("price");
        when(request.getParameter(RequestParameter.ORDER)).thenReturn("asc");

        servlet.doGet(request, response);

        verify(productDao).findProducts(eq("query"), eq(SortField.price), eq(SortOrder.asc));
        verify(request).getRequestDispatcher(eq(JspPage.PRODUCT_LIST));
    }

    @Test
    public void doGet_recentProducts_setRecentProductAttribute() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq(RequestAttribute.RECENT_PRODUCTS), eq(products));
        verify(request).getRequestDispatcher(eq(JspPage.PRODUCT_LIST));
    }

    @Test
    public void doPost_quantityNotNumber_setAddToCartErrorAttribute() throws ServletException, IOException {
        setLocale();
        setProductId();
        when(request.getParameter(RequestParameter.QUANTITY)).thenReturn(RequestParameter.QUANTITY);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq(RequestAttribute.ADD_TO_CART_ERROR), anyString());
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void doPost_quantityNotIntegerNumber_setAddToCartErrorAttribute() throws ServletException, IOException {
        setLocale();
        setProductId();
        when(request.getParameter(RequestParameter.QUANTITY)).thenReturn("1,1");

        servlet.doPost(request, response);

        verify(request).setAttribute(eq(RequestAttribute.ADD_TO_CART_ERROR), anyString());
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void doPost_productStockMoreThenQuantity_redirectWithSuccessMessage() throws ServletException, IOException {
        setLocale();
        setProductId();
        when(request.getParameter(RequestParameter.QUANTITY)).thenReturn("1");

        servlet.doPost(request, response);

        verify(response).sendRedirect(anyString());
    }

    @Test
    public void doPost_addToCartQuantityMoreThenAvailable_outOfStockException()
            throws OutOfStockException, ServletException, IOException {
        setLocale();
        setProductId();
        when(request.getParameter(RequestParameter.QUANTITY)).thenReturn("1");
        when(cartService.getCart(httpSession)).thenReturn(cart);
        doThrow(OutOfStockException.class).when(cartService)
                .add(cart, productId, 1);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq(RequestAttribute.ADD_TO_CART_ERROR), anyString());
        verify(response, never()).sendRedirect(anyString());
    }

    private void setProductId() {
        when(request.getParameter(RequestParameter.PRODUCT_ID)).thenReturn(productId.toString());
    }

    private void setLocale() {
        when(request.getLocale()).thenReturn(new Locale("ru"));
    }


    private void setRecentProductsService() throws IllegalAccessException, NoSuchFieldException {
        Field recentProductsServiceField = ProductListPageServlet.class.getDeclaredField("recentProductsService");
        recentProductsServiceField.setAccessible(true);
        recentProductsServiceField.set(servlet, recentProductsService);
    }

    private void setProductDao() throws IllegalAccessException, NoSuchFieldException {
        Field productDaoField = ProductListPageServlet.class.getDeclaredField("productDao");
        productDaoField.setAccessible(true);
        productDaoField.set(servlet, productDao);
    }

    private void setCartService() throws IllegalAccessException, NoSuchFieldException {
        Field cartServiceField = ProductListPageServlet.class.getDeclaredField("cartService");
        cartServiceField.setAccessible(true);
        cartServiceField.set(servlet, cartService);
    }

}