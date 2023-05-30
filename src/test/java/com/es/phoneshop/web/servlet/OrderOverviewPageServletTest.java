package com.es.phoneshop.web.servlet;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.dao.impl.ArrayListOrderDao;
import com.es.phoneshop.exception.order.OrderNotFoundException;
import com.es.phoneshop.model.order.Order;
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
import java.lang.reflect.Field;
import java.util.UUID;

import static com.es.phoneshop.web.constant.ServletConstant.JspPage;
import static com.es.phoneshop.web.constant.ServletConstant.RequestAttribute;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderOverviewPageServletTest {
    private final UUID orderId = UUID.randomUUID();
    private final OrderOverviewPageServlet servlet = new OrderOverviewPageServlet();
    @Mock
    private OrderDao orderDao = Mockito.mock(ArrayListOrderDao.class);
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig serverConfig;

    @Before
    public void setup() throws ServletException, NoSuchFieldException, IllegalAccessException {
        servlet.init(serverConfig);
        setOrderDao();
    }

    @Test
    public void doGet_ForwardToOrderOverviewJsp() throws ServletException, IOException {
        Order order = new Order();
        order.setId(orderId);
        setOrderSecureIdInRequestPath(orderId.toString());
        when(orderDao.getItem(orderId)).thenReturn(order);
        when(request.getRequestDispatcher(JspPage.ORDER_OVERVIEW)).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(RequestAttribute.ORDER, order);
        verify(request).getRequestDispatcher(JspPage.ORDER_OVERVIEW);
        verify(requestDispatcher).forward(request, response);
    }

    @Test(expected = OrderNotFoundException.class)
    public void doGet_nonexistentOrderSecureId_OrderNotFoundException() throws ServletException, IOException {
        setOrderSecureIdInRequestPath(orderId.toString());
        when(orderDao.getItem(orderId)).thenThrow(OrderNotFoundException.class);

        servlet.doGet(request, response);

        verify(orderDao).getItem(orderId);
    }

    @Test
    public void doGet_noOrderSecureIdInRequestPath_pageNotFound() throws ServletException, IOException {
        when(request.getRequestDispatcher(any())).thenReturn(requestDispatcher);
        setOrderSecureIdInRequestPath("");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void setOrderSecureIdInRequestPath(String id) {
        when(request.getPathInfo()).thenReturn("/" + id);
    }

    private void setOrderDao() throws IllegalAccessException, NoSuchFieldException {
        Field orderDaoField = OrderOverviewPageServlet.class.getDeclaredField("orderDao");
        orderDaoField.setAccessible(true);
        orderDaoField.set(servlet, orderDao);
    }

}
