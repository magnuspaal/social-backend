package com.magnus.social.controllers;

import com.magnus.social.auth.AuthenticationService;
import com.magnus.social.like.Like;
import com.magnus.social.like.LikeService;
import com.magnus.social.post.Post;
import com.magnus.social.post.PostService;
import com.magnus.social.post.dto.PostRequest;
import com.magnus.social.user.User;
import com.magnus.social.user.UserService;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

  public final AuthenticationService authenticationService;
  public final PostService postService;
  public final LikeService likeService;
  public final UserService userService;

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

  @PostMapping()
  public ResponseEntity<Post> createPost(@Valid @RequestBody PostRequest body) {
    User user = authenticationService.getAuthenticatedUser();
    Post post = postService.createPost(body.getContent(), user);
    return ResponseEntity.ok(post);
  }

  @PostMapping("/{id}/like")
  public ResponseEntity<Post> likePost(@PathVariable Long id) {
    User user = authenticationService.getAuthenticatedUser();
    Post post = postService.getPostById(id);
    post.updateLikes(likeService.toggleLike(user, post));
    return ResponseEntity.ok(post);
  }

  @PostMapping("/{id}/reply")
  public ResponseEntity<Post> replyToPost(@PathVariable Long id, @Valid @RequestBody PostRequest body) {
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
