package com.magnus.social.auth;

import com.magnus.social.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  public User getAuthenticatedUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  public void logout() {
    SecurityContextHolder.getContext().setAuthentication(null);
  }
}