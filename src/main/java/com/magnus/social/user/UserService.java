package com.magnus.social.user;

import com.magnus.social.follow.Follow;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public User getUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("user not found"));
  }

  public List<User> getUserFollowers(Long id) {
    User user = this.getUserById(id);
    List<Follow> follower = user.getFollowers();
    return follower.stream().map(Follow::getFollowedBy).toList();
  }

  public List<User> getUserFollowing(Long id) {
    User user = this.getUserById(id);
    List<Follow> follower = user.getFollowing();
    return follower.stream().map(Follow::getFollowed).toList();
  }

  public User updateOrCreateUser(User newUser) {
    User user = userRepository.findById(newUser.getId()).orElse(null);
    if (user == null) {
      return userRepository.save(newUser);
    } else {
      user.setEmail(newUser.getEmail());
      user.setFirstName(newUser.getFirstName());
      user.setLastName(newUser.getLastName());
      user.setUserRole(newUser.getUserRole());
      user.setUsername(newUser.getUsername());
      return userRepository.save(user);
    }
  }

  public List<User> getUsers() {
    return userRepository.findAll();
  }
}