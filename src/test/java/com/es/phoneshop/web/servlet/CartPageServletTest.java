package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.service.CartService;
import com.es.phoneshop.model.cart.service.DefaultCartService;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.RecentProducts;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CartPageServletTest {
    private static final String CART_JSP = "/WEB-INF/pages/cart.jsp";

    private static final String CART_REQUEST_ATTRIBUTE = "cart";
    private static final String QUANTITY_REQUEST_ATTRIBUTE = "quantity";
    private static final String PRODUCT_ID_REQUEST_ATTRIBUTE = "productId";
    private static final String RECENT_PRODUCTS_REQUEST_ATTRIBUTE = "recentProducts";

    private final CartPageServlet servlet = new CartPageServlet();
    private final long productId = 1L;
    private final Product product = new Product();
    @Mock
    private Deque<Product> products;
    @Mock
    private RecentProducts recentProducts = Mockito.mock(RecentProducts.class);
    @Mock
    private Cart cart;
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
        setCartService();
        setRecentProductsService();

        product.setId(productId);

        when(request.getSession()).thenReturn(httpSession);
        when(cartService.getCart(httpSession)).thenReturn(cart);
        when(recentProducts.getProducts()).thenReturn(products);
        when(recentProductsService.getRecentProducts(httpSession)).thenReturn(recentProducts);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void doGet_setAttributes() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).setAttribute(eq(CART_REQUEST_ATTRIBUTE), eq(cart));
        verify(request).setAttribute(eq(RECENT_PRODUCTS_REQUEST_ATTRIBUTE), eq(products));
    }

    @Test
    public void doGet_forwardToCart() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).getRequestDispatcher(eq(CART_JSP));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void doPost_validQuantity_sendRedirect() throws ServletException, IOException {
        beforePost();
        when(request.getParameterValues(QUANTITY_REQUEST_ATTRIBUTE)).thenReturn(new String[]{"1"});

        servlet.doPost(request, response);

        verify(response).sendRedirect(eq(String.format("%s/cart?message=%s", request.getContextPath(),
                "Cart updated successfully")));
    }

    @Test
    public void doPost_invalidQuantity_sendRedirect() throws ServletException, IOException {
        beforePost();
        when(request.getParameterValues(QUANTITY_REQUEST_ATTRIBUTE)).thenReturn(new String[]{"1,1"});

        servlet.doPost(request, response);

        verify(response, never()).sendRedirect(eq(String.format("%s/cart?message=%s", request.getContextPath(),
                "Cart updated successfully")));
        verify(request).getRequestDispatcher(eq(CART_JSP));
        verify(requestDispatcher).forward(request, response);
    }

    private void beforePost() {
        when(request.getLocale()).thenReturn(new Locale("ru"));
        when(request.getParameterValues(PRODUCT_ID_REQUEST_ATTRIBUTE))
                .thenReturn(new String[]{String.valueOf(productId)});
    }

    private void setCartService() throws IllegalAccessException, NoSuchFieldException {
        Field cartServiceField = CartPageServlet.class.getDeclaredField("cartService");
        cartServiceField.setAccessible(true);
        cartServiceField.set(servlet, cartService);
    }

    private void setRecentProductsService() throws IllegalAccessException, NoSuchFieldException {
        Field recentProductsServiceField = CartPageServlet.class.getDeclaredField("recentProductsService");
        recentProductsServiceField.setAccessible(true);
        recentProductsServiceField.set(servlet, recentProductsService);
    }

}
