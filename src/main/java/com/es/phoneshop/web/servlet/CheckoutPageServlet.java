package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.DefaultCartService;
import com.es.phoneshop.web.validation.impl.DefaultParameterValidationService;
import com.es.phoneshop.service.impl.DefaultOrderService;
import com.es.phoneshop.web.validation.ParameterValidationService;
import com.es.phoneshop.service.OrderService;
import static com.es.phoneshop.web.constant.ServletConstant.*;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CheckoutPageServlet extends HttpServlet {
    private CartService cartService;
    private OrderService orderService;
    private ParameterValidationService orderDataValidationService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        cartService = DefaultCartService.getInstance();
        orderService = DefaultOrderService.getInstance();
        orderDataValidationService = DefaultParameterValidationService.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request.getSession());
        setRequestAttributes(request, orderService.createOrder(cart, request.getLocale()), null);
        request.getRequestDispatcher(JspPage.CHECKOUT).forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Order order = orderService.createOrder(cartService.getCart(session), request.getLocale());
        Map<String, String> placeOrderErrors = new HashMap<>();
        setRequiredOrderParameters(request, order, placeOrderErrors);
        if (placeOrderErrors.isEmpty()) {
            orderService.placeOrder(order);
            cartService.clearCart(session);
            response.sendRedirect(getOrderOverviewPath(request, order));
            return;
        }
        setRequestAttributes(request, order, placeOrderErrors);
        request.getRequestDispatcher(JspPage.CHECKOUT).forward(request, response);
    }

    private void setRequiredOrderParameters(HttpServletRequest request, Order order, Map<String, String> orderErrors) {
        setRequiredOrderParameter(request, RequestParameter.Order.FIRST_NAME, orderErrors, order::setFirstName,
                orderDataValidationService::isValidRequiredStringData);
        setRequiredOrderParameter(request, RequestParameter.Order.LAST_NAME, orderErrors, order::setLastName,
                orderDataValidationService::isValidRequiredStringData);
        setRequiredOrderParameter(request, RequestParameter.Order.ADDRESS, orderErrors, order::setDeliveryAddress,
                orderDataValidationService::isValidRequiredStringData);
        setRequiredOrderParameter(request, RequestParameter.Order.PHONE, orderErrors, order::setPhone,
                orderDataValidationService::isValidPhone);
        setOrderPaymentMethod(request, order, orderErrors);
        setOrderDeliveryDate(request, order, orderErrors);
    }

    private void setRequiredOrderParameter(HttpServletRequest request, String parameterName, Map<String,
            String> orderErrors, Consumer<String> setOrderDate, Predicate<String> isValidOrderData) {
        String parameterValue = request.getParameter(parameterName);
        if (isValidOrderData.test(parameterValue)) {
            setOrderDate.accept(parameterValue);
        } else {
            orderErrors.put(parameterName, Message.REQUIRED_VALUE);
        }
    }

    private void setOrderPaymentMethod(HttpServletRequest request, Order order, Map<String, String> orderErrors) {
        String paymentMethod = request.getParameter(RequestParameter.Order.PAYMENT_METHOD);
        if (orderDataValidationService.isValidPaymentMethod(paymentMethod)) {
            order.setPaymentMethod(orderDataValidationService.parsePaymentMethod(paymentMethod));
        } else {
            orderErrors.put(RequestParameter.Order.PAYMENT_METHOD, Message.Error.PAYMENT_METHOD);
        }
    }

    private void setOrderDeliveryDate(HttpServletRequest request, Order order, Map<String, String> orderErrors) {
        Locale locale = request.getLocale();
        String deliveryDate = request.getParameter(RequestParameter.Order.DELIVERY_DATE);
        if (orderDataValidationService.isValidDeliveryDate(deliveryDate, locale)) {
            order.setDeliveryDate(orderDataValidationService.parseDate(deliveryDate, locale));
        } else {
            orderErrors.put(RequestParameter.Order.DELIVERY_DATE, Message.Error.DELIVERY_DATE);
        }
    }

    private String getOrderOverviewPath(HttpServletRequest request, Order order) {
        return String.format("%s/order/overview/%s", request.getContextPath(), order.getId());
    }

    private void setRequestAttributes(HttpServletRequest request, Order order, Map<String, String> placeOrderErrors) {
        request.setAttribute(RequestAttribute.ORDER, order);
        request.setAttribute(RequestParameter.Order.PAYMENT_METHOD, orderService.getPaymentMethods());
        request.setAttribute(RequestAttribute.PLACE_ORDER_ERRORS, placeOrderErrors);
    }

}
