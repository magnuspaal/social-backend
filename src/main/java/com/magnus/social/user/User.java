package com.magnus.social.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.magnus.social.common.BaseEntity;
import com.magnus.social.follow.Follow;
import com.magnus.social.like.Like;
import com.magnus.social.post.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_data")
@Where(clause = "deleted_at IS NULL")
public class User extends BaseEntity {

    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String imageName;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @OneToMany(mappedBy="user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Post> posts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "followedBy", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Follow> following;

    @OneToMany(mappedBy = "followed", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Follow> followers;

    @Transient
    private boolean followed;

    public User(Long id, String firstName, String lastName, String email, String username, UserRole userRole) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.userRole = userRole;
    }

    @Transient
    private Integer followerCount;

    @Transient
    private Integer followingCount;

    @PostLoad
    private void postLoad() {
        this.followerCount = this.getFollowers().size();
        this.followingCount = this.getFollowing().size();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            List<Follow> usersFollowers = followers.stream().filter((follower) -> Objects.equals(user.getId(), follower.getFollowedBy().getId())).toList();
            this.followed = !usersFollowers.isEmpty();
        }
    }

    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole.name());
        return Collections.singletonList(authority);
    }
}
