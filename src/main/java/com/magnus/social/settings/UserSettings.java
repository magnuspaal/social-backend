package com.magnus.social.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.magnus.social.common.BaseEntity;
import com.magnus.social.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    uniqueConstraints = { @UniqueConstraint(name = "UniqueUserAndKey", columnNames = { "user_id", "key" }) }
)
@Where(clause = "deleted_at IS NULL")
public class UserSettings extends BaseEntity {

  @Id
  @SequenceGenerator(
      name = "user_settings_sequence",
      sequenceName = "user_settings_sequence",
      allocationSize = 1
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "user_settings_sequence"
  )
  @JsonIgnore
  private Long id;
  @ManyToOne
  @JsonIgnore
  @JoinColumn(name="user_id")
  private User user;
  @Enumerated(EnumType.STRING)
  private UserSettingsKeys key;
  @Enumerated(EnumType.STRING)
  private UserSettingsValues value;

  public UserSettings(User user, UserSettingsKeys key, UserSettingsValues value) {
    this.user = user;
    this.key = key;
    this.value = value;
  }
}

