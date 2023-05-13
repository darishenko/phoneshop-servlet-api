package com.es.phoneshop.web;

import com.es.phoneshop.model.product.RecentProducts;
import com.es.phoneshop.model.product.sortEnum.SortField;
import com.es.phoneshop.model.product.sortEnum.SortOrder;
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
import java.util.Optional;

public class ProductListPageServlet extends HttpServlet {
    private static final String PRODUCT_LIST_JSP = "/WEB-INF/pages/productList.jsp";

    private static final String QUERY_REQUEST_PARAMETER = "query";
    private static final String SORT_REQUEST_PARAMETER = "sort";
    private static final String ORDER_REQUEST_PARAMETER = "order";

    private static final String PRODUCTS_REQUEST_ATTRIBUTE = "products";
    private static final String RECENT_PRODUCTS_REQUEST_ATTRIBUTE = "recentProducts";

    private ProductDao productDao;
    private RecentProductsService recentProductsService;

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }


    public void setRecentProductsService(RecentProductsService recentProductsService) {
        this.recentProductsService = recentProductsService;
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        productDao = ArrayListProductDao.getInstance();
        recentProductsService = DefaultRecentProductsService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        RecentProducts recentProducts = recentProductsService.getRecentProducts(session);
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

}
