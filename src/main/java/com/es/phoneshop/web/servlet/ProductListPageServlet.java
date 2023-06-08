package com.es.phoneshop.web.servlet;

import com.es.phoneshop.exception.product.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.DefaultCartService;
import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.service.impl.DefaultRecentProductsService;
import com.es.phoneshop.service.RecentProductsService;
import com.es.phoneshop.enam.sort.SortField;
import com.es.phoneshop.enam.sort.SortOrder;
import com.es.phoneshop.web.validation.ParameterValidationService;
import com.es.phoneshop.web.validation.impl.DefaultParameterValidationService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.ParseException;
import static com.es.phoneshop.web.constant.ServletConstant.*;
import java.util.*;

public class ProductListPageServlet extends HttpServlet {
    private ParameterValidationService parameterValidationService;
    private ProductDao productDao;
    private CartService cartService;
    private RecentProductsService recentProductsService;

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
        String query = request.getParameter(RequestParameter.QUERY);
        String sortField = request.getParameter(RequestParameter.SORT);
        String sortOrder = request.getParameter(RequestParameter.ORDER);

        setRequestAttribute(request, query, sortField, sortOrder);
        request.getRequestDispatcher(JspPage.PRODUCT_LIST).forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID productId = UUID.fromString(request.getParameter(RequestParameter.PRODUCT_ID));
        if (isSuccessfullyAddedToCart(request, productId)) {
            response.sendRedirect(getProductsListPagePathWithSuccessMessage(request));
        } else {
            doGet(request, response);
        }
    }

    private String getProductsListPagePathWithSuccessMessage(HttpServletRequest request) {
        return request.getContextPath() + "/products" + getQueryParameters(request) +
                String.format("&message=%s", Message.Success.ADD_PRODUCT_TO_CART);
    }

    private String getQueryParameters(HttpServletRequest request) {
        String query = request.getParameter(RequestParameter.QUERY);
        String sortField = request.getParameter(RequestParameter.SORT);
        String sortOrder = request.getParameter(RequestParameter.ORDER);

        StringJoiner parameters = new StringJoiner("&", "?", "");
        Optional.ofNullable(query).ifPresent(q -> parameters.add(RequestParameter.QUERY + "=" + q));
        Optional.ofNullable(sortField).ifPresent(field -> parameters.add(RequestParameter.SORT + "=" + field));
        Optional.ofNullable(sortOrder).ifPresent(order -> parameters.add(RequestParameter.ORDER + "=" + order));

        return parameters.toString();
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

    private void setAddToCartErrorRequestAttribute(HttpServletRequest request, String errorMessage) {
        request.setAttribute(RequestAttribute.ADD_TO_CART_ERROR, errorMessage);
    }

    private int getQuantityFromRequestParameter(HttpServletRequest request) throws ParseException {
        return parameterValidationService.parseQuantity(request.getParameter(RequestParameter.QUANTITY),
                request.getLocale());
    }

    private void setRequestAttribute(HttpServletRequest request, String query, String sortField, String sortOrder){
        request.setAttribute(RequestAttribute.PRODUCTS, productDao.findProducts(query,
                Optional.ofNullable(sortField).map(SortField::valueOf).orElse(null),
                Optional.ofNullable(sortOrder).map(SortOrder::valueOf).orElse(null), null, null, null)
        );
        request.setAttribute(RequestAttribute.RECENT_PRODUCTS, recentProductsService
                .getRecentProducts(request.getSession()).getProducts());
    }

}
