package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.DefaultCartService;
import static com.es.phoneshop.web.constant.ServletConstant.*;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

public class DeleteCartItemServlet extends HttpServlet {
    private CartService cartService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        cartService = DefaultCartService.getInstance();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UUID productId = getProductIdFromRequestPath(request);
        Cart cart = cartService.getCart(request.getSession());
        cartService.delete(cart, productId);

        response.sendRedirect(getCartPagePathWithSuccessMessage(request));
    }

    private UUID getProductIdFromRequestPath(HttpServletRequest request) {
        return UUID.fromString(request.getPathInfo().substring(1));
    }

    private String getCartPagePathWithSuccessMessage(HttpServletRequest request) {
        return String.format("%s/cart?message=%s", request.getContextPath(), Message.Success.REMOVE_CART_ITEM);
    }

}
