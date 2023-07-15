package com.magnus.social.follow;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.magnus.social.common.BaseEntity;
import com.magnus.social.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Where(clause = "deleted_at IS NULL")
@Table(
    uniqueConstraints = { @UniqueConstraint(name = "UniqueFollowedByAndFollowed", columnNames = { "followed_by", "followed", "deletedAt" }) }
)
public class Follow extends BaseEntity {

  @Id
  @SequenceGenerator(
      name = "follower_sequence",
      sequenceName = "follower_sequence",
      allocationSize = 1
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "follower_sequence"
  )
  private Long id;

  @ManyToOne
  @JoinColumn(name="followed_by")
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @JsonIdentityReference(alwaysAsId = true)
  @JsonProperty("followerId")
  private User followedBy;

  @ManyToOne
  @JoinColumn(name="followed")
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @JsonIdentityReference(alwaysAsId = true)
  @JsonProperty("followedId")
  private User followed;

  public Follow(User followedBy, User followed) {
    this.followedBy = followedBy;
    this.followed = followed;
  }
}
