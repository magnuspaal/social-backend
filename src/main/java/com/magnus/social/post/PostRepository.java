package com.magnus.social.post;

import com.magnus.social.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional()
public interface PostRepository extends JpaRepository<Post, Long> {

  @Query("SELECT p FROM Post p WHERE p.user IN :users OR (p.user=:user AND p.repostParent is NULL) ORDER BY p.createdAt DESC LIMIT :limit OFFSET :offset")
  Optional<List<Post>> findFeedPosts(
      @Param("user") User user,
      @Param("users") List<User> followingUsersList,
      @Param("limit") Integer limit,
      @Param("offset") Integer offset
  );

  @Query("SELECT p FROM Post p WHERE p.replyParent = :post ORDER BY p.createdAt DESC LIMIT :limit OFFSET :offset")
  Optional<List<Post>> findReplies(@Param("post") Post post, @Param("limit") Integer limit, @Param("offset") Integer offset);

  @Query("SELECT p FROM Post p WHERE p.user = :user ORDER BY p.createdAt DESC LIMIT :limit OFFSET :offset")
  Optional<List<Post>> findByUser(@Param("user") User user, @Param("limit") Integer limit, @Param("offset") Integer offset);

  @Query("SELECT p FROM Post p WHERE p.user = :user AND p.repostParent = :repostParent")
  Optional<Post> findPostByUserAndRepostParent(@Param("user") User user, @Param("repostParent") Post post);
}
