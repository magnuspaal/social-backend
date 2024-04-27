package com.magnus.social.controllers;

import com.magnus.social.auth.AuthenticationService;
import com.magnus.social.exception.exceptions.PostingNotAllowedException;
import com.magnus.social.services.FileService;
import com.magnus.social.like.LikeService;
import com.magnus.social.post.Post;
import com.magnus.social.post.PostService;
import com.magnus.social.post.dto.PostRequest;
import com.magnus.social.post.dto.ReplyResponse;
import com.magnus.social.settings.UserSettingsKeys;
import com.magnus.social.settings.UserSettingsValues;
import com.magnus.social.user.User;
import com.magnus.social.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
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
  public ResponseEntity<Post> createPost(@CookieValue("authToken") String authToken, @Valid PostRequest body) throws PostingNotAllowedException {
    User authenticatedUser = authenticationService.getAuthenticatedUser();
    User user = userService.getUserById(authenticatedUser.getId());
    if(!user.checkIfUserSettingExists(UserSettingsKeys.POSTING_DISALLOWED, UserSettingsValues.ENABLED)) {
      Post post;
      if (body.getImage() != null) {
        String filename = fileService.uploadFile(body.getImage(), authToken);
        post = postService.createImagePost(body.getContent(), filename, user);
      } else {
        post = postService.createPost(body.getContent(), user);
      }
      return ResponseEntity.ok(post);
    } else {
      throw new PostingNotAllowedException();
    }
  }

  @PostMapping("/{id}/like")
  public ResponseEntity<Post> likePost(@PathVariable Long id) {
    User user = authenticationService.getAuthenticatedUser();
    Post post = postService.getPostById(id);
    post.updateLikes(likeService.toggleLike(user, post));
    return ResponseEntity.ok(post);
  }

  @PostMapping(value = "/{id}/reply", consumes="multipart/form-data")
  public ResponseEntity<ReplyResponse> replyToPost(@PathVariable Long id, @Valid PostRequest body) throws PostingNotAllowedException {
    User user = authenticationService.getAuthenticatedUser();
    User dbUser = userService.getUserById(user.getId());

    if (!dbUser.checkIfUserSettingExists(UserSettingsKeys.POSTING_DISALLOWED, UserSettingsValues.ENABLED)) {
      Post post = postService.getPostById(id);
      Post reply = postService.replyToPost(post, dbUser, body.getContent());
      post.addReply();
      return ResponseEntity.ok(ReplyResponse.builder().reply(reply).replyParent(post).build());
    } else {
      throw new PostingNotAllowedException();
    }
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
