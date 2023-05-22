package com.es.phoneshop.web;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.RecentProducts;
import com.es.phoneshop.model.product.service.DefaultRecentProductsService;
import com.es.phoneshop.model.product.service.RecentProductsService;
import com.es.phoneshop.model.product.sortEnum.SortField;
import com.es.phoneshop.model.product.sortEnum.SortOrder;
import com.es.phoneshop.model.product.dao.ArrayListProductDao;
import com.es.phoneshop.model.product.dao.ProductDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductListPageServletTest {
    private static final String PRODUCT_LIST_JSP = "/WEB-INF/pages/productList.jsp";

    private static final String QUERY_REQUEST_PARAMETER = "query";
    private static final String SORT_REQUEST_PARAMETER = "sort";
    private static final String ORDER_REQUEST_PARAMETER = "order";

    private static final String PRODUCTS_REQUEST_ATTRIBUTE = "products";
    private static final String RECENT_PRODUCTS_REQUEST_ATTRIBUTE = "recentProducts";

    private final Deque<Product> products = new ArrayDeque<>();
    private final ProductListPageServlet servlet = new ProductListPageServlet();
    @Mock
    private RecentProducts recentProducts = Mockito.mock(RecentProducts.class);
    @Mock
    private ProductDao productDao = Mockito.mock(ArrayListProductDao.class);
    @Mock
    private RecentProductsService recentProductsService = Mockito.mock(DefaultRecentProductsService.class);
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig serverConfig;
    @Mock
    private HttpSession httpSession;

    @Before
    public void setup() throws ServletException {
        servlet.init(serverConfig);
        servlet.setProductDao(productDao);
        servlet.setRecentProductsService(recentProductsService);

        when(request.getSession()).thenReturn(httpSession);
        when(recentProducts.getProducts()).thenReturn(products);
        when(recentProductsService.getRecentProducts(httpSession)).thenReturn(recentProducts);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void doGet_setProductAttribute() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq(PRODUCTS_REQUEST_ATTRIBUTE), any());
        verify(request).getRequestDispatcher(eq(PRODUCT_LIST_JSP));
    }

    @Test
    public void doGet_getQuerySortOrder_executeFindProducts() throws ServletException, IOException {
        when(request.getParameter(QUERY_REQUEST_PARAMETER)).thenReturn("query");
        when(request.getParameter(SORT_REQUEST_PARAMETER)).thenReturn("price");
        when(request.getParameter(ORDER_REQUEST_PARAMETER)).thenReturn("asc");

        servlet.doGet(request, response);

        verify(productDao).findProducts(eq("query"), eq(SortField.price), eq(SortOrder.asc));
        verify(request).getRequestDispatcher(eq(PRODUCT_LIST_JSP));
    }

    @Test
    public void doGet_recentProducts_setRecentProductAttribute() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq(RECENT_PRODUCTS_REQUEST_ATTRIBUTE), eq(products));
        verify(request).getRequestDispatcher(eq(PRODUCT_LIST_JSP));
    }

}