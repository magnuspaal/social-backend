package com.magnus.social.follow;

import com.magnus.social.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FollowService {

  private final FollowRepository followRepository;

  public Follow toggleFollower(User followedBy, User followed) {
    Follow follow = followRepository.findByFollowedByAndFollowed(followedBy, followed).orElse(null);
    if (follow == null) {
      follow = new Follow(followedBy, followed);
    } else {
      follow.setDeletedAt(LocalDateTime.now());
    }
    return followRepository.save(follow);
  }
}