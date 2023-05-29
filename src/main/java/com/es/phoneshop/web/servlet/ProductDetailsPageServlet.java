package com.es.phoneshop.web.servlet;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.service.CartService;
import com.es.phoneshop.model.cart.service.DefaultCartService;
import com.es.phoneshop.model.cart.service.DefaultQuantityService;
import com.es.phoneshop.model.cart.service.QuantityService;
import com.es.phoneshop.model.product.RecentProducts;
import com.es.phoneshop.model.product.dao.ArrayListProductDao;
import com.es.phoneshop.model.product.dao.ProductDao;
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

public class ProductDetailsPageServlet extends HttpServlet {
    private static final String PRODUCT_DETAILS_JSP = "/WEB-INF/pages/productDetails.jsp";

    private static final String CART_REQUEST_ATTRIBUTE = "cart";
    private static final String PRODUCT_REQUEST_ATTRIBUTE = "product";
    private static final String QUANTITY_REQUEST_ATTRIBUTE = "quantity";
    private static final String RECENT_PRODUCTS_REQUEST_ATTRIBUTE = "recentProducts";
    private static final String ADD_TO_CART_ERROR_REQUEST_ATTRIBUTE = "addToCartError";
    private ProductDao productDao;
    private CartService cartService;
    private RecentProductsService recentProductsService;
    private QuantityService quantityService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
        recentProductsService = DefaultRecentProductsService.getInstance();
        quantityService = DefaultQuantityService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = getProductIdFromRequestPath(request, response);
        HttpSession session = request.getSession();
        RecentProducts recentProducts = recentProductsService.getRecentProducts(session);
        recentProductsService.addToRecentProducts(recentProducts, productId);

        request.setAttribute(PRODUCT_REQUEST_ATTRIBUTE, productDao.getProduct(productId));
        request.setAttribute(RECENT_PRODUCTS_REQUEST_ATTRIBUTE, recentProducts.getProducts());
        request.setAttribute(CART_REQUEST_ATTRIBUTE, cartService.getCart(session));
        request.getRequestDispatcher(PRODUCT_DETAILS_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = getProductIdFromRequestPath(request, response);
        if (isSuccessfullyAddedToCart(request, productId)) {
            response.sendRedirect(getProductDetailsPagePathWithSuccessMessage(request, productId));
        } else {
            doGet(request, response);
        }
    }

    private boolean isSuccessfullyAddedToCart(HttpServletRequest request, Long productId) {
        try {
            Cart cart = cartService.getCart(request.getSession());
            int quantity = getQuantityFromRequestParameter(request);
            cartService.add(cart, productId, quantity);
            return true;
        } catch (NumberFormatException numberFormatException) {
            setAddToCartErrorRequestAttribute(request, numberFormatException.getMessage());
            return false;
        } catch (ParseException parseException) {
            setAddToCartErrorRequestAttribute(request, "Not a number!");
            return false;
        } catch (OutOfStockException outOfStockException) {
            setAddToCartErrorRequestAttribute(request, String.format("Not enough items in stock! Available: %d",
                    outOfStockException.getAvailableQuantity()));
            return false;
        }
    }

    private String getProductDetailsPagePathWithSuccessMessage(HttpServletRequest request, Long productId) {
        return String.format("%s/products/%d?message=%s", request.getContextPath(), productId,
                "Product was added to cart");
    }

    private Long getProductIdFromRequestPath(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long productId = null;
        String requestPath = getRequestPath(request);
        if (requestPath.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            productId = Long.valueOf(requestPath);
        }
        return productId;
    }

    private String getRequestPath(HttpServletRequest request) {
        return request.getPathInfo().substring(1);
    }

    private void setAddToCartErrorRequestAttribute(HttpServletRequest request, String errorMessage) {
        request.setAttribute(ADD_TO_CART_ERROR_REQUEST_ATTRIBUTE, errorMessage);
    }

    private int getQuantityFromRequestParameter(HttpServletRequest request) throws ParseException {
        return quantityService.parseQuantity(request.getParameter(QUANTITY_REQUEST_ATTRIBUTE), request.getLocale());
    }

}