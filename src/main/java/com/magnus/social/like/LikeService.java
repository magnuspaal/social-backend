package com.magnus.social.like;

import com.magnus.social.follow.Follow;
import com.magnus.social.post.Post;
import com.magnus.social.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeService {

  private final LikeRepository likeRepository;

  public Like toggleLike(User user, Post post) {
    Like like = likeRepository.findByUserAndPost(user, post).orElse(null);
    if (like == null) {
      like = new Like(user, post);
    } else {
      like.setDeletedAt(LocalDateTime.now());
    }
    return likeRepository.save(like);
  }
}