package com.magnus.social.like;


import com.fasterxml.jackson.annotation.*;
import com.magnus.social.common.BaseEntity;
import com.magnus.social.post.Post;
import com.magnus.social.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

@Entity
@Table(
    name = "post_like",
    uniqueConstraints = { @UniqueConstraint(name = "UniqueUserAndPost", columnNames = { "user_id", "post_id", "deletedAt" }) }
)
@Getter
@Setter
@NoArgsConstructor
@Where(clause = "deleted_at IS NULL")
public class Like extends BaseEntity {

  @Id
  @SequenceGenerator(
      name = "like_sequence",
      sequenceName = "like_sequence",
      allocationSize = 1
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "like_sequence"
  )
  private Long id;

  @ManyToOne
  @JoinColumn(name="user_id")
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @JsonIdentityReference(alwaysAsId = true)
  @JsonProperty("userId")
  private User user;

  @ManyToOne
  @JoinColumn(name="post_id")
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @JsonIdentityReference(alwaysAsId = true)
  @JsonProperty("postId")
  private Post post;

  public Like(User user, Post post) {
    this.post = post;
    this.user = user;
  }
}
