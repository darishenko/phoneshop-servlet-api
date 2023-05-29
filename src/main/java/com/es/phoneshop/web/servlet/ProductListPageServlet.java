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
import com.es.phoneshop.model.product.sortEnum.SortField;
import com.es.phoneshop.model.product.sortEnum.SortOrder;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class ProductListPageServlet extends HttpServlet {
    private static final String PRODUCT_LIST_JSP = "/WEB-INF/pages/productList.jsp";

    private static final String QUERY_REQUEST_PARAMETER = "query";
    private static final String SORT_REQUEST_PARAMETER = "sort";
    private static final String ORDER_REQUEST_PARAMETER = "order";
    private static final String PRODUCT_ID_REQUEST_PARAMETER = "productId";
    private static final String QUANTITY_REQUEST_PARAMETER = "quantity";

    private static final String PRODUCTS_REQUEST_ATTRIBUTE = "products";
    private static final String RECENT_PRODUCTS_REQUEST_ATTRIBUTE = "recentProducts";
    private static final String ADD_TO_CART_ERROR_REQUEST_ATTRIBUTE = "addToCartError";
    private QuantityService quantityService;
    private ProductDao productDao;
    private CartService cartService;
    private RecentProductsService recentProductsService;

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
        RecentProducts recentProducts = recentProductsService.getRecentProducts(request.getSession());
        String query = request.getParameter(QUERY_REQUEST_PARAMETER);
        String sortField = request.getParameter(SORT_REQUEST_PARAMETER);
        String sortOrder = request.getParameter(ORDER_REQUEST_PARAMETER);

        request.setAttribute(PRODUCTS_REQUEST_ATTRIBUTE, productDao.findProducts(query,
                Optional.ofNullable(sortField).map(SortField::valueOf).orElse(null),
                Optional.ofNullable(sortOrder).map(SortOrder::valueOf).orElse(null))
        );
        request.setAttribute(RECENT_PRODUCTS_REQUEST_ATTRIBUTE, recentProducts.getProducts());
        request.getRequestDispatcher(PRODUCT_LIST_JSP).forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = Long.parseLong(request.getParameter(PRODUCT_ID_REQUEST_PARAMETER));
        if (isSuccessfullyAddedToCart(request, productId)) {
            response.sendRedirect(getProductsListPagePathWithSuccessMessage(request));
        } else {
            doGet(request, response);
        }
    }

    private String getProductsListPagePathWithSuccessMessage(HttpServletRequest request) {
        return request.getContextPath() + "/products" + getQueryParameters(request) +
                String.format("&message=%s", "Product was added to cart");
    }

    private String getQueryParameters(HttpServletRequest request) {
        String query = request.getParameter(QUERY_REQUEST_PARAMETER);
        String sortField = request.getParameter(SORT_REQUEST_PARAMETER);
        String sortOrder = request.getParameter(ORDER_REQUEST_PARAMETER);

        StringJoiner parameters = new StringJoiner("&", "?", "");
        Optional.ofNullable(query).ifPresent(q -> parameters.add(QUERY_REQUEST_PARAMETER + "=" + q));
        Optional.ofNullable(sortField).ifPresent(field -> parameters.add(SORT_REQUEST_PARAMETER + "=" + field));
        Optional.ofNullable(sortOrder).ifPresent(order -> parameters.add(ORDER_REQUEST_PARAMETER + "=" + order));

        return parameters.toString();
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

    private void setAddToCartErrorRequestAttribute(HttpServletRequest request, String errorMessage) {
        request.setAttribute(ADD_TO_CART_ERROR_REQUEST_ATTRIBUTE, errorMessage);
    }

    private int getQuantityFromRequestParameter(HttpServletRequest request) throws ParseException {
        return quantityService.parseQuantity(request.getParameter(QUANTITY_REQUEST_PARAMETER), request.getLocale());
    }

}
