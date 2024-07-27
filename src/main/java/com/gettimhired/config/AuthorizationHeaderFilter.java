package com.gettimhired.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AuthorizationHeaderFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            String headerValue = request.getHeader("Authorization");
            RequestContextHolder.setHeader(headerValue);
            chain.doFilter(request, response);
        } finally {
            RequestContextHolder.clear();
        }
    }
}
