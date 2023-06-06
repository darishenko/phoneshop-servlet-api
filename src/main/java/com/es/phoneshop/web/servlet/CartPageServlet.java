package com.es.phoneshop.web.servlet;

import com.es.phoneshop.exception.product.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.DefaultCartService;
import com.es.phoneshop.model.product.RecentProducts;
import com.es.phoneshop.service.impl.DefaultRecentProductsService;
import com.es.phoneshop.service.RecentProductsService;
import com.es.phoneshop.web.validation.ParameterValidationService;
import com.es.phoneshop.web.validation.impl.DefaultParameterValidationService;
import static com.es.phoneshop.web.constant.ServletConstant.*;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

public class CartPageServlet extends HttpServlet {
    private ParameterValidationService defaultParameterValidationService;
    private CartService cartService;
    private RecentProductsService recentProductsService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        cartService = DefaultCartService.getInstance();
        recentProductsService = DefaultRecentProductsService.getInstance();
        defaultParameterValidationService = DefaultParameterValidationService.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Cart cart = cartService.getCart(session);
        RecentProducts recentProducts = recentProductsService.getRecentProducts(session);

        request.setAttribute(RequestAttribute.CART, cart);
        request.setAttribute(RequestAttribute.RECENT_PRODUCTS, recentProducts.getProducts());
        request.getRequestDispatcher(JspPage.CART).forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<UUID, String> updateCartErrors = new HashMap<>();
        updateCart(request, updateCartErrors);
        if (updateCartErrors.isEmpty()) {
            response.sendRedirect(getCartPagePathWithSuccessMessage(request));
        } else {
            request.setAttribute(RequestAttribute.UPDATE_CART_ERRORS, updateCartErrors);
            doGet(request, response);
        }
    }

    private void updateCart(HttpServletRequest request, Map<UUID, String> updateCartErrors) {
        Locale locale = request.getLocale();
        Cart cart = cartService.getCart(request.getSession());
        String[] productIds = request.getParameterValues(RequestAttribute.PRODUCT_ID);
        String[] productQuantities = request.getParameterValues(RequestAttribute.QUANTITY);

        IntStream.range(0, productIds.length)
                .forEach(i -> updateCartItem(productIds[i], productQuantities[i], cart, updateCartErrors, locale));
    }

    private void updateCartItem(String productId, String productQuantity, Cart cart, Map<UUID, String> updateCartErrors,
                                Locale locale) {
        UUID id = UUID.fromString(productId);
        try {
            int quantity = defaultParameterValidationService.parseQuantity(productQuantity, locale);
            cartService.update(cart, id, quantity);
        } catch (NumberFormatException numberFormatException) {
            updateCartErrors.put(id, numberFormatException.getMessage());
        } catch (ParseException parseException) {
            updateCartErrors.put(id, Message.Error.NOT_A_NUMBER);
        } catch (OutOfStockException outOfStockException) {
            updateCartErrors.put(id, String.format("%s Available: %d", Message.Error.OUT_OF_STOCK,
                    outOfStockException.getAvailableQuantity()));
        }
    }

    private String getCartPagePathWithSuccessMessage(HttpServletRequest request) {
        return String.format("%s/cart?message=%s", request.getContextPath(), Message.Success.UPDATE_CART);
    }

}
