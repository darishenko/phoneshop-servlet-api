package com.es.phoneshop.web.servlet;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.service.CartService;
import com.es.phoneshop.model.cart.service.DefaultCartService;
import com.es.phoneshop.model.cart.service.DefaultQuantityService;
import com.es.phoneshop.model.cart.service.QuantityService;
import com.es.phoneshop.model.product.RecentProducts;
import com.es.phoneshop.model.product.service.DefaultRecentProductsService;
import com.es.phoneshop.model.product.service.RecentProductsService;
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
import java.util.stream.IntStream;

public class CartPageServlet extends HttpServlet {
    private static final String CART_JSP = "/WEB-INF/pages/cart.jsp";

    private static final String CART_REQUEST_ATTRIBUTE = "cart";
    private static final String QUANTITY_REQUEST_ATTRIBUTE = "quantity";
    private static final String PRODUCT_ID_REQUEST_ATTRIBUTE = "productId";
    private static final String RECENT_PRODUCTS_REQUEST_ATTRIBUTE = "recentProducts";
    private static final String UPDATE_CART_ERRORS_REQUEST_ATTRIBUTE = "updateCartErrors";
    private QuantityService quantityService;
    private CartService cartService;
    private RecentProductsService recentProductsService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        cartService = DefaultCartService.getInstance();
        recentProductsService = DefaultRecentProductsService.getInstance();
        quantityService = DefaultQuantityService.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Cart cart = cartService.getCart(session);
        RecentProducts recentProducts = recentProductsService.getRecentProducts(session);

        request.setAttribute(CART_REQUEST_ATTRIBUTE, cart);
        request.setAttribute(RECENT_PRODUCTS_REQUEST_ATTRIBUTE, recentProducts.getProducts());
        request.getRequestDispatcher(CART_JSP).forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<Long, String> updateCartErrors = new HashMap<>();
        updateCart(request, updateCartErrors);
        if (updateCartErrors.isEmpty()) {
            response.sendRedirect(getCartPagePathWithSuccessMessage(request));
        } else {
            request.setAttribute(UPDATE_CART_ERRORS_REQUEST_ATTRIBUTE, updateCartErrors);
            doGet(request, response);
        }
    }

    private void updateCart(HttpServletRequest request, Map<Long, String> updateCartErrors) {
        Locale locale = request.getLocale();
        Cart cart = cartService.getCart(request.getSession());
        String[] productIds = request.getParameterValues(PRODUCT_ID_REQUEST_ATTRIBUTE);
        String[] productQuantities = request.getParameterValues(QUANTITY_REQUEST_ATTRIBUTE);

        IntStream.range(0, productIds.length)
                .forEach(i -> updateCartItem(productIds[i], productQuantities[i], cart, updateCartErrors, locale));
    }

    private void updateCartItem(String productId, String productQuantity, Cart cart, Map<Long, String> updateCartErrors,
                                Locale locale) {
        Long id = Long.parseLong(productId);
        try {
            int quantity = quantityService.parseQuantity(productQuantity, locale);
            cartService.update(cart, id, quantity);
        } catch (NumberFormatException numberFormatException) {
            updateCartErrors.put(id, numberFormatException.getMessage());
        } catch (ParseException parseException) {
            updateCartErrors.put(id, "Not a number!");
        } catch (OutOfStockException outOfStockException) {
            updateCartErrors.put(id, String.format("Not enough items in stock! Available: %d",
                    outOfStockException.getAvailableQuantity()));
        }
    }

    private String getCartPagePathWithSuccessMessage(HttpServletRequest request) {
        return String.format("%s/cart?message=%s", request.getContextPath(), "Cart updated successfully");
    }

}
