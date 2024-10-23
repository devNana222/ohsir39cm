package com.tdd.ecommerce.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class XSSFilterTest {

    private XSSFilter sut = new XSSFilter();

    @Test
    @DisplayName("XSS 필터가 스크립트를 제거한다.")
    void testXssFilter() throws IOException, ServletException {
        //given
        String header = "<script>alert('XSSHeader');</script>";
        String param = "<script>alert('XSSParam');</script>";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("");
        request.setMethod("GET");
        request.addHeader("Header", header);
        request.addParameter("param", param);

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        //when
        XSSRequestWrapper wrappedRequest = new XSSRequestWrapper(request);

        sut.doFilter(request, response, filterChain);

        //then
        String filteredContent = wrappedRequest.getParameter("param");
        assertFalse(filteredContent.contains("<script>"));
        assertTrue(filteredContent.contains("& lt;& gt;alert& #40;"));
    }
}
