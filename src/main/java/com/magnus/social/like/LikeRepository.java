package com.magnus.social.like;

import com.magnus.social.follow.Follow;
import com.magnus.social.post.Post;
import com.magnus.social.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional()
public interface LikeRepository extends JpaRepository<Like, Long> {

  public Optional<Like> findByUserAndPost(User user, Post post);
}