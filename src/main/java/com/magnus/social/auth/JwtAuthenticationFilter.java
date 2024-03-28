package com.magnus.social.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  @Override
  public void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
      throws IOException, ServletException {
    final String authHeader = request.getHeader("Authorization");
    Cookie cookie = null;

    if (request.getCookies() != null) {
      cookie = Arrays.stream(request.getCookies()).filter(x -> x.getName().equals("authToken")).findFirst().orElse(null);
    }

    final String jwt;

    if (cookie != null && cookie.getValue() != null) {
      jwt = cookie.getValue();
    } else if (authHeader != null && authHeader.startsWith("Bearer")) {
      jwt = authHeader.substring(7);
    } else {
      filterChain.doFilter(request, response);
      return;
    }
    Authentication authToken = jwtService.validateJWT(jwt);
    if (authToken != null) {
      SecurityContextHolder.getContext().setAuthentication(authToken);
    }
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request)
      throws ServletException {
    String path = request.getRequestURI();
    return path.startsWith("/api/v1/auth/confirm/");
  }
}
