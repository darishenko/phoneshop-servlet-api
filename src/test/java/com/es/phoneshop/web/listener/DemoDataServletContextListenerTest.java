package com.es.phoneshop.web.listener;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.ProductDao;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DemoDataServletContextListenerTest {
    private final DemoDataServletContextListener demoDataServletContextListener = new DemoDataServletContextListener();
    private final String INSERT_DEMO_DATA = "insertDemoData";
    @Mock
    private ProductDao productDao;
    @Mock
    private ServletContextEvent servletContextEvent;
    @Mock
    private ServletContext servletContext;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        when(servletContextEvent.getServletContext()).thenReturn(servletContext);
        setProductDaoField();
    }

    @Test
    public void contextInitialized_insertDemoData_getSampleProducts() {
        when(servletContextEvent.getServletContext().getInitParameter(INSERT_DEMO_DATA)).thenReturn("true");

        demoDataServletContextListener.contextInitialized(servletContextEvent);

        verify(productDao, atLeastOnce()).save(any(Product.class));
    }

    @Test
    public void contextInitialized_notInsertDemoData_getSampleProducts() {
        when(servletContextEvent.getServletContext().getInitParameter(INSERT_DEMO_DATA)).thenReturn("false");

        demoDataServletContextListener.contextInitialized(servletContextEvent);

        verify(productDao, never()).save(any(Product.class));
    }

    private void setProductDaoField() throws IllegalAccessException, NoSuchFieldException {
        Field productDaoField = DemoDataServletContextListener.class.getDeclaredField("productDao");
        productDaoField.setAccessible(true);
        productDaoField.set(demoDataServletContextListener, productDao);
    }
}
