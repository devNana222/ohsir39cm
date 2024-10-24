package com.tdd.ecommerce.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;

@Slf4j
public class CorsFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {
        // 필터 객체를 초기화하고 서비스에 추가하기 위한 메소드.
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //url-pattern에 맞는 모든 HTTP 요청이 디스패처 서블릿으로 전달되기 전에 웹 컨테이너에 의해 실행되는 메소드.
        // FilterChain : FilterChain의 doFilter를 통해 다음 대상으로 요청을 전달하게 됨.
        // chain.doFilter() 전/후에 우리가 필요한 처리과정을 넣어줌으로써 원하는 처리를 진행할 수 있다.
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        chain.doFilter(request, response);
        log.info("CorsFilter run");
    }

    public void destroy() {
        // 필터 객체를 서비스에서 제거하고 사용하는 자원을 반환하기 위한 메소드.
        // 웹 컨테이너에 의해 1번 호출되며 이후에는 doFilter에 의해 처리되지 않는다.
    }
}//스프링 내부에 있는거 사용


