package com.es.phoneshop.web.filter;

import com.es.phoneshop.security.impl.DefaultDosProtectionService;
import com.es.phoneshop.security.DosProtectionService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
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

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DosFilterTest {
    private final Filter filter = new DosFilter();
    @Mock
    private DosProtectionService dosProtectionService = Mockito.mock(DefaultDosProtectionService.class);
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        setDosProtectionService();
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    }

    @Test
    public void doFilter_allowed_doFilter() throws ServletException, IOException {
        when(dosProtectionService.isAllowed(request.getRemoteAddr())).thenReturn(Boolean.TRUE);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void doFilter_notAllowed_setStatusCode() throws ServletException, IOException {
        when(dosProtectionService.isAllowed(request.getRemoteAddr())).thenReturn(Boolean.FALSE);

        filter.doFilter(request, response, filterChain);

        verify(response).setStatus(eq(429));
    }

    private void setDosProtectionService() throws IllegalAccessException, NoSuchFieldException {
        Field dosProtectionServiceField = DosFilter.class.getDeclaredField("dosProtectionService");
        dosProtectionServiceField.setAccessible(true);
        dosProtectionServiceField.set(filter, dosProtectionService);
    }

}
