package com.es.phoneshop.web.servlet;

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

public class ProductPriceHistoryPageServlet extends HttpServlet {
    private static final String PRODUCT_PRICE_HISTORY_JSP = "/WEB-INF/pages/productPriceHistory.jsp";

    private static final String PRODUCT_REQUEST_ATTRIBUTE = "product";
    private static final String RECENT_PRODUCTS_REQUEST_ATTRIBUTE = "recentProducts";

    private ProductDao productDao;
    private RecentProductsService recentProductsService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        productDao = ArrayListProductDao.getInstance();
        recentProductsService = DefaultRecentProductsService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        Long productId = getProductIdFromRequestPath(request, response);
        HttpSession session = request.getSession();
        RecentProducts recentProducts =  recentProductsService.getRecentProducts(session);

        request.setAttribute(RECENT_PRODUCTS_REQUEST_ATTRIBUTE, recentProducts.getProducts());
        request.setAttribute(PRODUCT_REQUEST_ATTRIBUTE, productDao.getProduct(productId));
        request.getRequestDispatcher(PRODUCT_PRICE_HISTORY_JSP).forward(request, response);
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

}
