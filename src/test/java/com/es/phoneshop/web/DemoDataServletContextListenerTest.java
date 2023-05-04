package com.es.phoneshop.web;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.ProductDao;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DemoDataServletContextListenerTest {
    private final DemoDataServletContextListener demoDataServletContextListener = new DemoDataServletContextListener();
    private final String insertDemoData = "insertDemoData";
    @Mock
    private ProductDao productDao;
    @Mock
    private ServletContextEvent servletContextEvent;
    @Mock
    private ServletContext servletContext;

    @Before
    public void setup() {
        when(servletContextEvent.getServletContext()).thenReturn(servletContext);
        demoDataServletContextListener.setProductDao(productDao);
    }

    @Test
    public void contextInitialized_insertDemoData_getSampleProducts() {
        when(servletContextEvent.getServletContext().getInitParameter(insertDemoData)).thenReturn("true");

        demoDataServletContextListener.contextInitialized(servletContextEvent);

        verify(productDao, atLeastOnce()).save(any(Product.class));
    }

    @Test
    public void contextInitialized_notInsertDemoData_getSampleProducts() {
        when(servletContextEvent.getServletContext().getInitParameter(insertDemoData)).thenReturn("false");

        demoDataServletContextListener.contextInitialized(servletContextEvent);

        verify(productDao, never()).save(any(Product.class));
    }
}
