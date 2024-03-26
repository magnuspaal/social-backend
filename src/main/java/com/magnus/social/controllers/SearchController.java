package com.magnus.social.controllers;

import com.magnus.social.search.dto.Search;
import com.magnus.social.search.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

  private final SearchService searchService;
  @GetMapping("/{search}")
  public ResponseEntity<Search> getSearch(@PathVariable String search) {
    return ResponseEntity.ok(searchService.search("%" + search + "%"));
  }
}
