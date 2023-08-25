package com.magnus.social.post.dto;

import com.magnus.social.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyResponse {
  private Post replyParent;
  private Post reply;
}