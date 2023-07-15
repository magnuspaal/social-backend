package com.magnus.social.post;

import com.fasterxml.jackson.annotation.*;
import com.magnus.social.common.BaseEntity;
import com.magnus.social.like.Like;
import com.magnus.social.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    uniqueConstraints = { @UniqueConstraint(name = "UniqueUserAndRepost", columnNames = { "user_id", "repost_parent_id", "deletedAt" }) }
)
@Where(clause = "deleted_at IS NULL")
public class Post extends BaseEntity {
  @Id
  @SequenceGenerator(
      name = "post_sequence",
      sequenceName = "post_sequence",
      allocationSize = 1
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "post_sequence"
  )
  private Long id;
  private String content;

  @ManyToOne
  @JoinColumn(name="user_id")
  private User user;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Like> likes = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name="reply_parent_id")
  private Post replyParent;

  @OneToMany(mappedBy = "replyParent")
  @JsonIgnore
  private List<Post> replies;

  @ManyToOne
  @JoinColumn(name="repost_parent_id")
  private Post repostParent;

  @OneToMany(mappedBy = "repostParent")
  @JsonIgnore
  private List<Post> reposts;

  @Transient
  private Integer likeCount = 0;

  @Transient
  private Integer repostCount = 0;

  @Transient
  private boolean liked = false;

  @Transient
  private boolean reposted = false;

  @PostLoad
  private void postLoad() {
    this.likeCount = this.getLikes().size();
    this.repostCount = this.getReposts().size();

    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    List<Like> usersLiked = likes.stream().filter((like) -> Objects.equals(like.getUser().getId(), user.getId())).toList();
    this.liked = !usersLiked.isEmpty();

    List<Post> usersReposted = reposts.stream().filter((repost) -> Objects.equals(repost.getUser().getId(), user.getId())).toList();
    this.reposted = !usersReposted.isEmpty();
  }

  public Post(String content, User user) {
    this.content = content;
    this.user = user;
  }

  public Post(User user) {
    this.user = user;
  }

  public void updateLikes(Like updatedLike) {
    if (updatedLike.getDeletedAt() == null) {
      this.likeCount += 1;
      this.liked = true;
    } else {
      this.likeCount -= 1;
      this.liked = false;
    }
  }

  public void updateReposts(Post updatedRepost) {
    if (updatedRepost.getDeletedAt() == null) {
      this.repostCount += 1;
      this.reposted = true;
    } else {
      this.repostCount -= 1;
      this.reposted = false;
    }
  }
}
