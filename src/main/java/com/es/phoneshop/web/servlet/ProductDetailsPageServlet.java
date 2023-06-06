package com.es.phoneshop.web.servlet;

import com.es.phoneshop.exception.product.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.DefaultCartService;
import com.es.phoneshop.model.product.RecentProducts;
import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
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
import java.util.UUID;

public class ProductDetailsPageServlet extends HttpServlet {
    private ProductDao productDao;
    private CartService cartService;
    private RecentProductsService recentProductsService;
    private ParameterValidationService parameterValidationService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
        recentProductsService = DefaultRecentProductsService.getInstance();
        parameterValidationService = DefaultParameterValidationService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UUID productId = getProductIdFromRequestPath(request, response);
        HttpSession session = request.getSession();
        RecentProducts recentProducts = recentProductsService.getRecentProducts(session);
        recentProductsService.addToRecentProducts(recentProducts, productId);

        request.setAttribute(RequestAttribute.PRODUCT, productDao.getItem(productId));
        request.setAttribute(RequestAttribute.RECENT_PRODUCTS, recentProducts.getProducts());
        request.setAttribute(RequestAttribute.CART, cartService.getCart(session));
        request.getRequestDispatcher(JspPage.PRODUCT_DETAILS).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UUID productId = getProductIdFromRequestPath(request, response);
        if (isSuccessfullyAddedToCart(request, productId)) {
            response.sendRedirect(getProductDetailsPagePathWithSuccessMessage(request, productId));
        } else {
            doGet(request, response);
        }
    }

    private boolean isSuccessfullyAddedToCart(HttpServletRequest request, UUID productId) {
        try {
            Cart cart = cartService.getCart(request.getSession());
            int quantity = getQuantityFromRequestParameter(request);
            cartService.add(cart, productId, quantity);
            return true;
        } catch (NumberFormatException numberFormatException) {
            setAddToCartErrorRequestAttribute(request, numberFormatException.getMessage());
            return false;
        } catch (ParseException parseException) {
            setAddToCartErrorRequestAttribute(request, Message.Error.NOT_A_NUMBER);
            return false;
        } catch (OutOfStockException outOfStockException) {
            setAddToCartErrorRequestAttribute(request, String.format("%s Available: %d", Message.Error.OUT_OF_STOCK,
                    outOfStockException.getAvailableQuantity()));
            return false;
        }
    }

    private String getProductDetailsPagePathWithSuccessMessage(HttpServletRequest request, UUID productId) {
        return String.format("%s/products/%s?message=%s", request.getContextPath(), productId.toString(),
                Message.Success.ADD_PRODUCT_TO_CART);
    }

    private UUID getProductIdFromRequestPath(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        UUID productId = null;
        String requestPath = getRequestPath(request);
        if (requestPath.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            productId = UUID.fromString(requestPath);
        }
        return productId;
    }

    private String getRequestPath(HttpServletRequest request) {
        return request.getPathInfo().substring(1);
    }

    private void setAddToCartErrorRequestAttribute(HttpServletRequest request, String errorMessage) {
        request.setAttribute(RequestAttribute.ADD_TO_CART_ERROR, errorMessage);
    }

    private int getQuantityFromRequestParameter(HttpServletRequest request) throws ParseException {
        return parameterValidationService.parseQuantity(request.getParameter(RequestAttribute.QUANTITY),
                request.getLocale());
    }

}