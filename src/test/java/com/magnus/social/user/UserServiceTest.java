package com.magnus.social.user;

import com.magnus.social.follow.Follow;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;


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

  @Nested
  class getUserById {
    @Test
    public void given_UserId_When_GetUserByIdCalled_Then_ReturnUser() {
      Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "John", "Doe", "john.doe@gmail.com", "john.doe", UserRole.USER)));

      User user = userService.getUserById(1L);
      assertEquals(user.getUsername(), "john.doe");
    }

    @Test
    public void given_UserIdOfUserThatDoesNotExist_When_GetUserByIdCalled_Then_ThrowIllegalArgumentException() {
      Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

      assertThrows(IllegalArgumentException.class, () -> {
        userService.getUserById(1l);
      });
    }
  }

  @Nested
  class getUserFollowers {
    @Test
    public void given_UserId_When_GetUserFollowersCalled_Then_ReturnUserFollowers() {
      User userFollowed = new User(1L, "John", "Doe", "john.doe@gmail.com", "john.doe", UserRole.USER);
      User userFollowedBy = new User(2L, "John", "Poe", "john.poe@gmail.com", "john.poe", UserRole.USER);
      userFollowed.setFollowers(List.of(new Follow(userFollowedBy, userFollowed)));
      Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userFollowed));
      List<User> followers = userService.getUserFollowers(1L);
      assertEquals(followers, List.of(userFollowedBy));
    }

    @Test
    public void given_UserIdWithNoFollowers_When_GetUserFollowersCalled_Then_ReturnEmptyList() {
      User userFollowed = new User(1L, "John", "Doe", "john.doe@gmail.com", "john.doe", UserRole.USER);
      Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userFollowed));
      userFollowed.setFollowers(List.of());
      List<User> followers = userService.getUserFollowers(1L);
      assertEquals(followers, List.of());
    }
  }

  @Nested
  class getUserFollowing {
    @Test
    public void given_UserId_When_GetUserFollowingCalled_Then_ReturnUserFollowingList() {
      User user = new User(1L, "John", "Doe", "john.doe@gmail.com", "john.doe", UserRole.USER);
      User followedUser = new User(2L, "John", "Poe", "john.poe@gmail.com", "john.poe", UserRole.USER);
      user.setFollowing(List.of(new Follow(user, followedUser)));
      Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      List<User> followers = userService.getUserFollowing(1L);
      assertEquals(followers, List.of(followedUser));
    }
  }

  @Nested
  class updateOrCreateUser {
    @Test
    public void given_UserThatExists_When_UpdateOrCreateUserCalled_Then_ReturnUpdatedUser() {
      User user = new User(1L, "John", "Doe", "john.doe@gmail.com", "john.doe", UserRole.USER);
      Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      Mockito.when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
      User newUser = new User(1L, "Paul", "Doe", "paul.doe@gmail.com", "paul.doe", UserRole.USER);
      User updatedUser = userService.updateOrCreateUser(newUser);
      assertEquals(updatedUser.getEmail(), "paul.doe@gmail.com");
      assertEquals(updatedUser.getFirstName(), "Paul");
      assertEquals(updatedUser.getLastName(), "Doe");
      assertEquals(updatedUser.getUsername(), "paul.doe");
    }

    @Test
    public void given_UserThatDoesNotExist_When_UpdateOrCreateUserCalled_Then_SaveAndReturnUser() {
      User user = new User(1L, "John", "Doe", "john.doe@gmail.com", "john.doe", UserRole.USER);
      Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      Mockito.when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
      User savedUser = userService.updateOrCreateUser(user);
      assertEquals(savedUser, user);
    }
  }

  @Nested
  class getUsers {
    @Test
    public void given_AllUsers_When_GetUsersCalled_Then_ReturnAllUsers() {
      User user1 = new User(1L, "John", "Doe", "john.doe@gmail.com", "john.doe", UserRole.USER);
      User user2 = new User(2L, "John", "Poe", "john.poe@gmail.com", "john.poe", UserRole.USER);
      Mockito.when(userRepository.findAll()).thenReturn(List.of(user1, user2));
      List<User> users = userService.getUsers();
      assertEquals(users, List.of(user1, user2));
    }
  }
}
