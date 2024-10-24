package com.tdd.ecommerce.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //컨트롤러가 호출되기 전에 실행된다. 컨트롤러 이전에 처리해야 하는 전처리 작업이나 요청정보를 가공하거나 추가하는 경우에 사용.
        // 핸들러 매핑이 찾아준 컨트롤러 빈에 매핑되는 HandlerMethod라는 타입의 객체, @RequestMapping이 붙은 메소드의 정보를 추상화한 객체

        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
