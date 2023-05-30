package com.es.phoneshop.web.servlet;

import com.es.phoneshop.dao.impl.ArrayListOrderDao;
import com.es.phoneshop.dao.OrderDao;
import static com.es.phoneshop.web.constant.ServletConstant.*;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

public class OrderOverviewPageServlet extends HttpServlet {
    private OrderDao orderDao;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        orderDao = ArrayListOrderDao.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID orderId = getOrderIdFromRequestPath(request, response);
        request.setAttribute(RequestAttribute.ORDER, orderDao.getItem(orderId));
        request.getRequestDispatcher(JspPage.ORDER_OVERVIEW).forward(request, response);
    }

    private UUID getOrderIdFromRequestPath(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        UUID orderId = null;
        String orderIdFromPath = getRequestPath(request);
        if (orderIdFromPath.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }else{
            orderId = UUID.fromString(orderIdFromPath);
        }
        return orderId;
    }

    private String getRequestPath(HttpServletRequest request) {
        return request.getPathInfo().substring(1);
    }

}
