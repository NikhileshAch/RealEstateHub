package ch.unil.doplab.webapplication.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

// Used AI to help write this code - Servlet filter for role-based access control

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"*.xhtml"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String requestURI = httpRequest.getRequestURI();
        
        // Allow access to login, register, and index pages
        if (requestURI.contains("login.xhtml") || 
            requestURI.contains("register.xhtml") || 
            requestURI.contains("index.xhtml") ||
            requestURI.contains("role-selection.xhtml") ||
            requestURI.contains("/jakarta.faces.resource/")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Check if user is logged in
        boolean loggedIn = (session != null && session.getAttribute("user") != null);
        
        if (loggedIn) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.xhtml");
        }
    }

    @Override
    public void destroy() {
    }
}
