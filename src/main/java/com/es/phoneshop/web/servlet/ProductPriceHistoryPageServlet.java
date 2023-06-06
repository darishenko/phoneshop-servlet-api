package com.es.phoneshop.web.servlet;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.model.product.RecentProducts;
import com.es.phoneshop.service.RecentProductsService;
import com.es.phoneshop.service.impl.DefaultRecentProductsService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.UUID;

import static com.es.phoneshop.web.constant.ServletConstant.JspPage;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute;

public class ProductPriceHistoryPageServlet extends HttpServlet {
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
        UUID productId = getProductIdFromRequestPath(request, response);
        HttpSession session = request.getSession();
        RecentProducts recentProducts = recentProductsService.getRecentProducts(session);

        request.setAttribute(RequestAttribute.RECENT_PRODUCTS, recentProducts.getProducts());
        request.setAttribute(RequestAttribute.PRODUCT, productDao.getItem(productId));
        request.getRequestDispatcher(JspPage.PRODUCT_PRICE_HISTORY).forward(request, response);
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

}
