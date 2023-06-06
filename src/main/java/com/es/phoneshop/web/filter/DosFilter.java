package com.es.phoneshop.web.filter;

import com.es.phoneshop.security.impl.DefaultDosProtectionService;
import com.es.phoneshop.security.DosProtectionService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class DosFilter implements Filter {
    private final static int TOO_MANY_REQUESTS_HTTP_STATUS = 429;
    private DosProtectionService dosProtectionService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        dosProtectionService = DefaultDosProtectionService.getInstance();
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        if (dosProtectionService.isAllowed(servletRequest.getRemoteAddr())) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            ((HttpServletResponse) servletResponse).setStatus(TOO_MANY_REQUESTS_HTTP_STATUS);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
