package com.magnus.social.controllers;

import com.magnus.social.auth.AuthenticationService;
import com.magnus.social.file.FileService;
import com.magnus.social.like.LikeService;
import com.magnus.social.post.Post;
import com.magnus.social.post.PostService;
import com.magnus.social.post.dto.PostRequest;
import com.magnus.social.user.User;
import com.magnus.social.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

  public final AuthenticationService authenticationService;
  public final PostService postService;
  public final LikeService likeService;
  public final UserService userService;
  public final FileService fileService;

  /*
   * ---------------
   *  GET REQUESTS
   * ---------------
   */

  @GetMapping("/{id}")
  public ResponseEntity<Post> getPost(@PathVariable Long id) {
    return ResponseEntity.ok(postService.getPostById(id));
  }

  @GetMapping("/feed")
  public ResponseEntity<List<Post>> getFeedPosts(@RequestParam("limit") Integer limit, @RequestParam("offset") Integer offset) {
    User user = authenticationService.getAuthenticatedUser();
    List<User> following = userService.getUserFollowing(user.getId());
    return ResponseEntity.ok(postService.getFeedPosts(user, following, limit, offset));
  }

  @GetMapping("/{id}/replies")
  public ResponseEntity<List<Post>> getPostReplies(@PathVariable Long id, @RequestParam("limit") Integer limit, @RequestParam("offset") Integer offset) {
    Post post = postService.getPostById(id);
    return ResponseEntity.ok(postService.getPostReplies(post, limit, offset));
  }

  /*
   * ---------------
   *  POST REQUESTS
   * ---------------
  */

  @PostMapping(consumes="multipart/form-data")
  public ResponseEntity<Post> createPost(@Valid PostRequest body) {
    User authenticatedUser = authenticationService.getAuthenticatedUser();
    User user = userService.getUserById(authenticatedUser.getId());
    Post post;
    if (body.getImage() != null) {
      String filename = fileService.uploadFile(body.getImage());
      post = postService.createImagePost(body.getContent(), filename, user);
    } else {
      post = postService.createPost(body.getContent(), user);
    }
    return ResponseEntity.ok(post);
  }

  @PostMapping("/{id}/like")
  public ResponseEntity<Post> likePost(@PathVariable Long id) {
    User user = authenticationService.getAuthenticatedUser();
    Post post = postService.getPostById(id);
    post.updateLikes(likeService.toggleLike(user, post));
    return ResponseEntity.ok(post);
  }

  @PostMapping(value = "/{id}/reply", consumes="multipart/form-data")
  public ResponseEntity<Post> replyToPost(@PathVariable Long id, @Valid PostRequest body) {
    User user = authenticationService.getAuthenticatedUser();
    Post post = postService.getPostById(id);
    return ResponseEntity.ok(postService.replyToPost(post, user, body.getContent()));
  }

  @PostMapping("/{id}/repost")
  public ResponseEntity<Post> repostPost(@PathVariable Long id) {
    User user = authenticationService.getAuthenticatedUser();
    Post post = postService.getPostById(id);
    Post repost = postService.repostPost(post, user);
    post.updateReposts(repost);
    return ResponseEntity.ok(repost);
  }
}
