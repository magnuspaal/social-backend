package com.magnus.social.exception.dto;

import com.magnus.social.common.BaseResponse;
import lombok.*;

import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ApiExceptionResponse extends BaseResponse {
  private ArrayList<String> codes;
}