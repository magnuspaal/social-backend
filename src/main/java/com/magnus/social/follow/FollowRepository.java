package com.magnus.social.follow;

import com.magnus.social.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional()
public interface FollowRepository extends JpaRepository<Follow, Long> {

  public Optional<Follow> findByFollowedByAndFollowed(User followedBy, User followed);

}