package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.service.CartService;
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
import java.util.UUID;

import static com.es.phoneshop.web.constant.ServletConstant.Message;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteCartItemServletTest {
    private final DeleteCartItemServlet servlet = new DeleteCartItemServlet();
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
    private HttpSession httpSession;

    @Before
    public void setup() throws ServletException, NoSuchFieldException, IllegalAccessException {
        servlet.init(serverConfig);
        setCartServiceField();
    }

    @Test
    public void doPost_productId_redirectToCartPageWithSuccessMessage() throws IOException {
        UUID productId = UUID.randomUUID();
        when(request.getPathInfo()).thenReturn("/" + productId);
        when(request.getSession()).thenReturn(httpSession);
        when(cartService.getCart(httpSession)).thenReturn(cart);

        servlet.doPost(request, response);

        verify(response).sendRedirect(eq(String.format("%s/cart?message=%s", request.getContextPath(),
                Message.Success.REMOVE_CART_ITEM)));
    }

    private void setCartServiceField() throws IllegalAccessException, NoSuchFieldException {
        Field cartServiceField = DeleteCartItemServlet.class.getDeclaredField("cartService");
        cartServiceField.setAccessible(true);
        cartServiceField.set(servlet, cartService);
    }

}
