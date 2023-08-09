package com.magnus.social.post;

import com.magnus.social.user.User;
import com.magnus.social.user.UserService;
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

  /*
   * GET METHODS
   */
  public Post getPostById(Long id) {
    return postRepository.findById(id).orElseThrow(() -> new NoSuchElementException("post not found"));
  }

  public List<Post> getFeedPosts(User user, List<User> following, Integer limit, Integer offset) {
    return postRepository.findFeedPosts(user, following, limit, offset).orElse(new ArrayList<>());
  }

  public List<Post> getPostReplies(Post post, Integer limit, Integer offset) {
    return postRepository.findReplies(post, limit, offset).orElse(new ArrayList<>());
  }

  public List<Post> getUserPosts(User user, Integer limit, Integer offset) {
    return postRepository.findByUser(user, limit, offset).orElse(new ArrayList<>());
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
    Post repost = postRepository.findRepostByUserAndRepostParent(user, post).orElse(null);
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
