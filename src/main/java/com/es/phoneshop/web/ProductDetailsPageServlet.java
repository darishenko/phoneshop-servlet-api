package com.es.phoneshop.web;

import com.es.phoneshop.model.product.dao.ArrayListProductDao;
import com.es.phoneshop.model.product.dao.ProductDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

public class ProductDetailsPageServlet extends HttpServlet {
    private ProductDao productDao;

    public ProductDao getProductDao() {
        return productDao;
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestPath = request.getPathInfo().substring(1);
        if (requestPath.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        String[] pathParameters = requestPath.split("/");
        Long productId = Optional.of(pathParameters[0]).map(Long::valueOf).orElse(null);
        request.setAttribute("product", productDao.getProduct(productId));
        if (pathParameters.length == 1) {
            request.getRequestDispatcher("/WEB-INF/pages/productDetails.jsp").forward(request, response);
        } else if ("priceHistory".equals(pathParameters[1])) {
            request.getRequestDispatcher("/WEB-INF/pages/productPriceHistory.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}