package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.service.CartService;
import com.es.phoneshop.model.cart.service.DefaultCartService;
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
import java.text.NumberFormat;
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

    public ProductDao getProductDao() {
        return productDao;
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    public void setRecentProductsService(RecentProductsService recentProductsService) {
        this.recentProductsService = recentProductsService;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
        recentProductsService = DefaultRecentProductsService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cart cart = cartService.getCart(request.getSession());
        Long productId = getProductIdFromRequestPath(request, response);
        try {
            Number quantity = parseQuantityRequestParameter(request);
            if (isValidQuantity(quantity)){
                cartService.add(cart, productId, quantity.intValue());
                redirectToProductDetailsPageWithSuccessMessage(request, response, productId);
                return;
            }else{
                setAddToCartErrorRequestAttribute(request, "Not a valid number!");
            }
        } catch (ParseException parseException) {
            setAddToCartErrorRequestAttribute(request, "Not a number!");
        } catch (OutOfStockException outOfStockException) {
            setAddToCartErrorRequestAttribute(request,
                    String.format("Not enough items in stock! Available: %d",
                            outOfStockException.getAvailableQuantity())
            );
        }
        doGet(request, response);
    }

    private void redirectToProductDetailsPageWithSuccessMessage(HttpServletRequest request, HttpServletResponse response,
                                                                Long productId) throws IOException {
        String redirectPath = String.format("%s/products/%d?message=%s", request.getContextPath(), productId,
                "Product was added to cart");
        response.sendRedirect(redirectPath);
    }

    private Number parseQuantityRequestParameter(HttpServletRequest request) throws ParseException {
        NumberFormat numberFormat = NumberFormat.getInstance(request.getLocale());
        String quantityString = request.getParameter(QUANTITY_REQUEST_ATTRIBUTE);
        return numberFormat.parse(quantityString);
    }

    private boolean isValidQuantity(Number quantity) {
        return quantity.intValue() > 0 && quantity.floatValue() % 1 == 0;
    }

    private Long getProductIdFromRequestPath(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Long productId = null;
        String requestPath = getRequestPath(request);
        if (requestPath.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }else{
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

}