package com.magnus.social.controllers;

import com.magnus.social.auth.AuthenticationService;
import com.magnus.social.follow.Follow;
import com.magnus.social.follow.FollowService;
import com.magnus.social.post.Post;
import com.magnus.social.post.PostService;
import com.magnus.social.file.FileService;
import com.magnus.social.user.User;
import com.magnus.social.user.UserRepository;
import com.magnus.social.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final FollowService followerService;
  private final PostService postService;
  private final AuthenticationService authenticationService;
  private final FileService fileService;
  private final UserRepository userRepository;

  @GetMapping("/me")
  public ResponseEntity<User> getAuthenticatedUser() {
    User user = authenticationService.getAuthenticatedUser();
    return ResponseEntity.ok(userService.getUserById(user.getId()));
  }

  /*
   * ---------------
   *  GET REQUESTS
   * ---------------
   */
  @GetMapping("/{id}")
  public ResponseEntity<User> getUser(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @GetMapping("/{id}/posts")
  public ResponseEntity<List<Post>> getUserPosts(@PathVariable Long id, @RequestParam Integer limit, @RequestParam Integer offset) {
    User user = userService.getUserById(id);
    return ResponseEntity.ok(postService.getUserPosts(user, limit, offset));
  }

  @GetMapping("/{id}/followers")
  public ResponseEntity<List<User>> getUserFollowers(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserFollowers(id));
  }

  @GetMapping("/{id}/following")
  public ResponseEntity<List<User>> getUserFollowing(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserFollowing(id));
  }

  @GetMapping()
  public ResponseEntity<List<User>> getUsers() {
    User authenticatedUser = authenticationService.getAuthenticatedUser();
    List<User> users = userService.getUsers().stream().filter((user) -> !Objects.equals(authenticatedUser.getId(), user.getId())).toList();
    return ResponseEntity.ok(users);
  }

  /*
   * ---------------
   *  POST REQUESTS
   * ---------------
   */
  @PostMapping("/{id}/follow")
  public ResponseEntity<Follow> followUser(@PathVariable Long id) {
    User followedBy = authenticationService.getAuthenticatedUser();
    User followed = userService.getUserById(id);
    return ResponseEntity.ok(followerService.toggleFollower(followedBy, followed));
  }

  @PostMapping("/{id}/upload-image")
  public ResponseEntity<User> uploadImage(
      @PathVariable Long id,
      @RequestParam(name = "image") MultipartFile image
  ) {
    User user = authenticationService.getAuthenticatedUser();
    if (!Objects.equals(user.getId(), id)) {
      return ResponseEntity.status(403).build();
    }
    // Upload file
    String fileName = fileService.uploadFile(image);
    User dbUser = userService.getUserById(user.getId());
    String imageName = dbUser.getImageName();
    // Delete existing file
    if (imageName != null) {
      try {
        fileService.deleteFile(imageName);
      } catch (HttpClientErrorException e) {
        return ResponseEntity.status(e.getStatusCode()).build();
      }
    }

    dbUser.setImageName(fileName);
    return ResponseEntity.ok(userRepository.save(dbUser));
  }
}
