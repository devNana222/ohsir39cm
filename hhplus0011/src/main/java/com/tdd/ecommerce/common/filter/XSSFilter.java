package com.tdd.ecommerce.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class XSSFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("XSSFilter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        XSSRequestWrapper xssRequestWrapper = new XSSRequestWrapper(httpRequest);
        chain.doFilter(xssRequestWrapper, response);
        log.warn("Someone try XSS");
    }

    @Override
    public void destroy() {
        log.info("XSSFilter destroy");
    }
}
