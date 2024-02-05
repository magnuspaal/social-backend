package com.magnus.social.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
public class UserServiceTest {

  @TestConfiguration
  static class UserServiceTestContextConfiguration {
    @Bean
    public UserService userService() {
      return new UserService();
    }
  }

  @Autowired
  private UserService userService;

  @MockBean
  private UserRepository userRepository;

  @Test
  public void given_UserId_When_GetUserByIdCalled_Then_ReturnUser() {
    Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "John", "Doe", "john.doe@gmail.com", "john.doe", UserRole.USER)));

    User user = userService.getUserById(1L);
    assertEquals(user.getUsername(), "john.doe");
  }
}
