package com.magnus.social.post;

import com.magnus.social.auth.AuthenticationService;
import com.magnus.social.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class PostService {
  private final PostRepository postRepository;
  private final AuthenticationService authenticationService;

  /*
   * GET METHODS
   */
  public Post getPostById(Long id) {
    User user = authenticationService.getAuthenticatedUser();
    Post post = postRepository.findById(id).orElseThrow(() -> new NoSuchElementException("post not found"));
    setIfUserHasReposted(user, post);
    return post;
  }

  public List<Post> getFeedPosts(User user, List<User> following, Integer limit, Integer offset) {
    List<Post> posts = postRepository.findFeedPosts(user, following, limit, offset).orElse(new ArrayList<>());
    for (Post post: posts) { setIfUserHasReposted(user, post); }
    return posts;
  }

  public List<Post> getPostReplies(Post post, Integer limit, Integer offset) {
    User user = authenticationService.getAuthenticatedUser();
    List<Post> posts = postRepository.findReplies(post, limit, offset).orElse(new ArrayList<>());
    for (Post listPost: posts) { setIfUserHasReposted(user, listPost); }
    return posts;
  }

  public List<Post> getUserPosts(User user, Integer limit, Integer offset) {
    User authenticatedUser = authenticationService.getAuthenticatedUser();
    List<Post> posts = postRepository.findByUser(user, limit, offset).orElse(new ArrayList<>());
    for (Post post: posts) { setIfUserHasReposted(authenticatedUser, post); }
    return posts;
  }

  private void setIfUserHasReposted(User user, Post post) {
    if (post.getReposts().size() > 0) {
      Post repost = postRepository.findPostByUserAndRepostParent(user, post).orElse(null);
      post.setReposted(repost != null);
    }
  }

  /*
   * CREATE METHODS
   */

  public Post createPost(String content, User user) {
    Post post = new Post(content, user);
    return postRepository.save(post);
  }

  public Post createImagePost(String content, String imageName, User user) {
    Post post = new Post(content, imageName, user);
    return postRepository.save(post);
  }

  public Post replyToPost(Post post, User user, String content) {
    Post reply = new Post(content, user);
    reply.setReplyParent(post);
    return postRepository.save(reply);
  }

  public Post repostPost(Post post, User user) {
    Post repost = postRepository.findPostByUserAndRepostParent(user, post).orElse(null);
    if (repost == null) {
      Post newRepost = new Post(user);
      newRepost.setRepostParent(post);
      repost = postRepository.save(newRepost);
    } else {
      repost.setDeletedAt(LocalDateTime.now());
      repost = postRepository.save(repost);
    }
    return repost;
  }
}
