package com.es.phoneshop.web;

import com.es.phoneshop.model.product.SortField;
import com.es.phoneshop.model.product.SortOrder;
import com.es.phoneshop.model.product.dao.ArrayListProductDao;
import com.es.phoneshop.model.product.dao.ProductDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductListPageServletTest {
    private final ProductListPageServlet servlet = new ProductListPageServlet();
    @Mock
    private final ProductDao productDao = Mockito.mock(ArrayListProductDao.class);
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig serverConfig;

    @Before
    public void setup() throws ServletException {
        servlet.init(serverConfig);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        servlet.setProductDao(productDao);
    }

    @Test
    public void doGet_setProductAttribute() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("products"), any());
    }

    @Test
    public void doGet_getQuerySortOrder_executeFindProducts() throws ServletException, IOException {
        when(request.getParameter("query")).thenReturn("query");
        when(request.getParameter("sort")).thenReturn("price");
        when(request.getParameter("order")).thenReturn("asc");

        servlet.doGet(request, response);

        verify(productDao).findProducts(eq("query"), eq(SortField.price), eq(SortOrder.asc));
    }

}