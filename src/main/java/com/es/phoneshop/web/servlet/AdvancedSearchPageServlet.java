package com.es.phoneshop.web.servlet;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.enam.search.AcceptanceCriteria;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.web.constant.ServletConstant.*;
import com.es.phoneshop.web.validation.ParameterValidationService;
import com.es.phoneshop.web.validation.impl.DefaultParameterValidationService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class AdvancedSearchPageServlet extends HttpServlet {
    private ProductDao productDao;
    private ParameterValidationService parameterValidationService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        productDao = ArrayListProductDao.getInstance();
        parameterValidationService = DefaultParameterValidationService.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> queryParameters = getQueryRequestParameters(request);
        if (definedQueryRequestParameters(queryParameters)) {
            Locale locale = request.getLocale();
            Map<String, String> queryParametersErrors = new HashMap<>();
            BigDecimal minPrice = getPriceFromRequestParameter(queryParameters, RequestParameter.MIN_PRICE,
                    queryParametersErrors, locale);
            BigDecimal maxPrice = getPriceFromRequestParameter(queryParameters, RequestParameter.MAX_PRICE,
                    queryParametersErrors, locale);
            parameterValidationService.validateMinMaxPrices(minPrice, maxPrice, queryParametersErrors);
            if (queryParametersErrors.isEmpty()) {
                serProductsRequestAttribute(request, queryParameters, minPrice, maxPrice);
            } else {
                request.setAttribute(RequestAttribute.PRICE_ERRORS, queryParametersErrors);
            }
        }
        request.setAttribute(RequestAttribute.ACCEPTANCE_CRITERIA, AcceptanceCriteria.values());
        request.getRequestDispatcher(JspPage.ADVANCED_SEARCH).forward(request, response);
    }

    private void serProductsRequestAttribute(HttpServletRequest request, Map<String, String> queryParameters,
                                             BigDecimal minPrice, BigDecimal maxPrice) {
        request.setAttribute(RequestAttribute.PRODUCTS,
                findProducts(queryParameters.get(RequestParameter.QUERY), minPrice, maxPrice,
                        AcceptanceCriteria.valueOf(queryParameters.get(RequestParameter.ACCEPTANCE_CRITERIA)))
        );
    }

    private List<Product> findProducts(String query, BigDecimal minPrice, BigDecimal maxPrice,
                                       AcceptanceCriteria criteria) {
        return productDao.findProducts(query, null, null, minPrice, maxPrice, criteria);
    }

    private BigDecimal getPriceFromRequestParameter( Map<String, String> queryParameters, String priceParameterName,
                                                     Map<String, String> queryParametersErrors, Locale locale){
        String price = queryParameters.get(priceParameterName);
        return parameterValidationService.parsePrice(price, priceParameterName, queryParametersErrors, locale);
    }

    private Map<String, String> getQueryRequestParameters(HttpServletRequest request) {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(RequestParameter.QUERY, request.getParameter(RequestParameter.QUERY));
        queryParameters.put(RequestParameter.ACCEPTANCE_CRITERIA,
                request.getParameter(RequestParameter.ACCEPTANCE_CRITERIA));
        queryParameters.put(RequestParameter.MIN_PRICE, request.getParameter(RequestParameter.MIN_PRICE));
        queryParameters.put(RequestParameter.MAX_PRICE, request.getParameter(RequestParameter.MAX_PRICE));
        return queryParameters;
    }

    private boolean definedQueryRequestParameters(Map<String, String> queryParameters){
        return Objects.nonNull(queryParameters.get(RequestParameter.QUERY))
                && Objects.nonNull(queryParameters.get(RequestParameter.ACCEPTANCE_CRITERIA))
                && Objects.nonNull(queryParameters.get(RequestParameter.MIN_PRICE))
                && Objects.nonNull(queryParameters.get(RequestParameter.MAX_PRICE));
    }

}
