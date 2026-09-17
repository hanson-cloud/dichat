package com.diqin.cloud.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GatewayCompatibleFilter extends OncePerRequestFilter {

    private static final Pattern API_DOCS =
            Pattern.compile("^/(admin-api|app-api|device-api)/([^/]+)/v3/api-docs$");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain chain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        Matcher matcher = API_DOCS.matcher(uri);

        if (matcher.matches()) {

            request.getRequestDispatcher("/v3/api-docs")
                    .forward(request, response);

            return;
        }

        chain.doFilter(request, response);

    }

}