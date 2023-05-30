package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.OrderService;
import com.es.phoneshop.service.impl.DefaultCartService;
import com.es.phoneshop.service.impl.DefaultOrderService;
import com.es.phoneshop.web.validation.ParameterValidationService;
import com.es.phoneshop.web.validation.impl.DefaultParameterValidationService;
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
import java.util.Locale;

import static com.es.phoneshop.web.constant.ServletConstant.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutPageServletTest {
    private final CheckoutPageServlet servlet = new CheckoutPageServlet();
    private final Locale locale = new Locale("ru");
    @Mock
    private Cart cart;
    @Mock
    private Order order;
    @Mock
    private CartService cartService = Mockito.mock(DefaultCartService.class);
    @Mock
    private OrderService orderService = Mockito.mock(DefaultOrderService.class);
    @Mock
    private ParameterValidationService parameterValidationService;
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

        parameterValidationService = Mockito.mock(DefaultParameterValidationService.class);
        setServices();

        when(request.getSession()).thenReturn(httpSession);
        when(request.getLocale()).thenReturn(locale);
        when(cartService.getCart(httpSession)).thenReturn(cart);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void doGet_setAttributes() throws ServletException, IOException {
        when(orderService.createOrder(cart, locale)).thenReturn(order);

        servlet.doGet(request, response);

        verifySetRequestAttribute();
    }

    @Test
    public void doGet_forwardToCheckoutPage() throws ServletException, IOException {
        when(orderService.createOrder(cart, locale)).thenReturn(order);

        servlet.doGet(request, response);

        verify(request).getRequestDispatcher(eq(JspPage.CHECKOUT));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void doPost_invalidOrderParameters_setParameters() throws ServletException, IOException {
        when(orderService.createOrder(cart, locale)).thenReturn(order);

        servlet.doPost(request, response);

        verifySetRequestAttribute();
    }

    @Test
    public void doPost_validOrderParameters_redirectToOrderOverview() throws ServletException, IOException {
        when(orderService.createOrder(cart, locale)).thenReturn(order);
        setOrderDataValidationResult(Boolean.TRUE);

        servlet.doPost(request, response);

        verify(response).sendRedirect(anyString());
        verify(request, never()).getRequestDispatcher(JspPage.CHECKOUT);
    }

    @Test
    public void doPost_invalidOrderParameters_forwardToCheckoutPage() throws ServletException, IOException {
        when(orderService.createOrder(cart, locale)).thenReturn(order);
        setOrderDataValidationResult(Boolean.FALSE);

        servlet.doPost(request, response);

        verify(request).getRequestDispatcher(JspPage.CHECKOUT);
        verify(response, never()).sendRedirect(anyString());
    }

    private void setOrderDataValidationResult(Boolean validationResult) {
        when(parameterValidationService.isValidRequiredStringData(any())).thenReturn(validationResult);
        when(parameterValidationService.isValidPaymentMethod(any())).thenReturn(validationResult);
        when(parameterValidationService.isValidPhone(any())).thenReturn(validationResult);
        when(parameterValidationService.isValidDeliveryDate(any(), any())).thenReturn(validationResult);
    }

    private void verifySetRequestAttribute() {
        verify(request).setAttribute(eq(RequestAttribute.ORDER), eq(order));
        verify(request).setAttribute(eq(RequestParameter.Order.PAYMENT_METHOD), any());
        verify(request).setAttribute(eq(RequestAttribute.PLACE_ORDER_ERRORS), any());
    }

    private void setCartService() throws IllegalAccessException, NoSuchFieldException {
        Field cartServiceField = CheckoutPageServlet.class.getDeclaredField("cartService");
        cartServiceField.setAccessible(true);
        cartServiceField.set(servlet, cartService);
    }

    private void setOrderService() throws IllegalAccessException, NoSuchFieldException {
        Field orderServiceField = CheckoutPageServlet.class.getDeclaredField("orderService");
        orderServiceField.setAccessible(true);
        orderServiceField.set(servlet, orderService);
    }

    private void setOrderDataValidationService() throws IllegalAccessException, NoSuchFieldException {
        Field orderDataValidationServiceField = CheckoutPageServlet.class.getDeclaredField("orderDataValidationService");
        orderDataValidationServiceField.setAccessible(true);
        orderDataValidationServiceField.set(servlet, parameterValidationService);
    }

    private void setServices() throws NoSuchFieldException, IllegalAccessException {
        setCartService();
        setOrderService();
        setOrderDataValidationService();
    }

}
