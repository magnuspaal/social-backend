package com.magnus.social.search;

import com.magnus.social.search.dto.Search;
import com.magnus.social.user.User;
import com.magnus.social.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

  private final UserRepository userRepository;

  public Search search(String keyword) {
    List<User> users = userRepository.findByUsernameLike(keyword).orElseThrow();
    users.sort((user1, user2) -> user2.getFollowerCount() - user1.getFollowerCount());
    return new Search(users);
  };
}
