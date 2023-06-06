package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.service.CartService;
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
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.lang.reflect.Field;

import static com.es.phoneshop.web.constant.ServletConstant.JspPage;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MiniCartServletTest {
    private final MiniCartServlet servlet = new MiniCartServlet();
    @Mock
    private CartService cartService;
    @Mock
    private Cart cart;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletConfig serverConfig;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private HttpSession httpSession;

    @Before
    public void setup() throws ServletException, NoSuchFieldException, IllegalAccessException {
        servlet.init(serverConfig);
        setCartServiceField();

        when(request.getSession()).thenReturn(httpSession);
        when(cartService.getCart(httpSession)).thenReturn(cart);
        when(request.getRequestDispatcher(JspPage.MINI_CART)).thenReturn(requestDispatcher);
    }

    @Test
    public void doGet_setRequestCartAttribute() throws IOException, ServletException {
        servlet.doGet(request, response);

        verify(request).setAttribute(RequestAttribute.CART, cart);
    }

    @Test
    public void doGet_includeRequest() throws IOException, ServletException {
        servlet.doGet(request, response);

        verify(requestDispatcher).include(request, response);
    }

    private void setCartServiceField() throws IllegalAccessException, NoSuchFieldException {
        Field cartServiceField = MiniCartServlet.class.getDeclaredField("cartService");
        cartServiceField.setAccessible(true);
        cartServiceField.set(servlet, cartService);
    }

}
