package com.es.phoneshop.web.servlet;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.service.CartService;
import com.es.phoneshop.model.cart.service.DefaultCartService;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.RecentProducts;
import com.es.phoneshop.model.product.service.DefaultRecentProductsService;
import com.es.phoneshop.model.product.service.RecentProductsService;
import com.es.phoneshop.model.product.sortEnum.SortField;
import com.es.phoneshop.model.product.sortEnum.SortOrder;
import com.es.phoneshop.model.product.dao.ArrayListProductDao;
import com.es.phoneshop.model.product.dao.ProductDao;
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
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class ProductListPageServletTest {
    private static final String PRODUCT_LIST_JSP = "/WEB-INF/pages/productList.jsp";

    private static final String QUERY_REQUEST_PARAMETER = "query";
    private static final String SORT_REQUEST_PARAMETER = "sort";
    private static final String ORDER_REQUEST_PARAMETER = "order";
    private static final String PRODUCT_ID_REQUEST_PARAMETER = "productId";
    private static final String QUANTITY_REQUEST_PARAMETER = "quantity";

    private static final String PRODUCTS_REQUEST_ATTRIBUTE = "products";
    private static final String RECENT_PRODUCTS_REQUEST_ATTRIBUTE = "recentProducts";
    private static final String ADD_TO_CART_ERROR_REQUEST_ATTRIBUTE = "addToCartError";

    private final ProductListPageServlet servlet = new ProductListPageServlet();
    private final Long productId = 1L;
    @Mock
    private  Deque<Product> products;
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
        verify(request).setAttribute(eq(PRODUCTS_REQUEST_ATTRIBUTE), any());
        verify(request).getRequestDispatcher(eq(PRODUCT_LIST_JSP));
    }

    @Test
    public void doGet_getQuerySortOrder_executeFindProducts() throws ServletException, IOException {
        when(request.getParameter(QUERY_REQUEST_PARAMETER)).thenReturn("query");
        when(request.getParameter(SORT_REQUEST_PARAMETER)).thenReturn("price");
        when(request.getParameter(ORDER_REQUEST_PARAMETER)).thenReturn("asc");

        servlet.doGet(request, response);

        verify(productDao).findProducts(eq("query"), eq(SortField.price), eq(SortOrder.asc));
        verify(request).getRequestDispatcher(eq(PRODUCT_LIST_JSP));
    }

    @Test
    public void doGet_recentProducts_setRecentProductAttribute() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq(RECENT_PRODUCTS_REQUEST_ATTRIBUTE), eq(products));
        verify(request).getRequestDispatcher(eq(PRODUCT_LIST_JSP));
    }

    @Test
    public void doPost_quantityNotNumber_setAddToCartErrorAttribute() throws ServletException, IOException {
        setLocale();
        setProductId();
        when(request.getParameter(QUANTITY_REQUEST_PARAMETER)).thenReturn(QUANTITY_REQUEST_PARAMETER);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq(ADD_TO_CART_ERROR_REQUEST_ATTRIBUTE), anyString());
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void doPost_quantityNotIntegerNumber_setAddToCartErrorAttribute() throws ServletException, IOException {
        setLocale();
        setProductId();
        when(request.getParameter(QUANTITY_REQUEST_PARAMETER)).thenReturn("1,1");

        servlet.doPost(request, response);

        verify(request).setAttribute(eq(ADD_TO_CART_ERROR_REQUEST_ATTRIBUTE), anyString());
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void doPost_productStockMoreThenQuantity_redirectWithSuccessMessage() throws ServletException, IOException {
        setLocale();
        setProductId();
        when(request.getParameter(QUANTITY_REQUEST_PARAMETER)).thenReturn("1");

        servlet.doPost(request, response);

        verify(response).sendRedirect(anyString());
    }

    @Test
    public void doPost_addToCartQuantityMoreThenAvailable_outOfStockException() throws OutOfStockException, ServletException, IOException {
        setLocale();
        setProductId();
        when(request.getParameter(QUANTITY_REQUEST_PARAMETER)).thenReturn("1");
        when(cartService.getCart(httpSession)).thenReturn(cart);
        doThrow(OutOfStockException.class).when(cartService).add(cart, productId, 1);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq(ADD_TO_CART_ERROR_REQUEST_ATTRIBUTE), anyString());
        verify(response, never()).sendRedirect(anyString());
    }

    private void setProductId(){
        when(request.getParameter(PRODUCT_ID_REQUEST_PARAMETER)).thenReturn(productId.toString());
    }

    private void setLocale(){
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