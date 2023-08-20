package com.magnus.social.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional()
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<List<User>> findByUsernameLike(String username);
}
