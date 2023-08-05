package com.magnus.social.auth;

import com.magnus.social.user.User;
import com.magnus.social.user.UserRepository;
import com.magnus.social.user.UserRole;
import com.magnus.social.user.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserService userService;

  @Override
  public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws IOException, ServletException {
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String userEmail;
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    jwt = authHeader.substring(7);
    userEmail = jwtService.extractUsername(jwt);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (userEmail != null && authentication != null) {
      String authenticatedEmail = ((User) authentication.getPrincipal()).getEmail();
      if (!userEmail.equals(authenticatedEmail)) {
        SecurityContextHolder.getContext().setAuthentication(null);
      }
    }
    authentication = SecurityContextHolder.getContext().getAuthentication();
    if (userEmail != null && authentication == null) {
      if (jwtService.isTokenValid(jwt)) {
        Claims claims = jwtService.extractAllClaims(jwt);

        User user = new User(
          claims.get("id", Long.class),
          claims.get("first_name", String.class),
          claims.get("last_name", String.class),
          claims.getSubject(),
            claims.get("username", String.class),
          UserRole.valueOf(claims.get("role", String.class))
        );

        userService.updateOrCreateUser(user);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            user,
            null,
            user.getAuthorities()
        );
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
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
