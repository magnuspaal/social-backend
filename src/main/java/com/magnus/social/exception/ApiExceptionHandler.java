package com.magnus.social.exception;

import com.magnus.social.common.BaseResponse;
import com.magnus.social.exception.dto.ApiExceptionResponse;
import com.magnus.social.exception.exceptions.PostingNotAllowedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(PostingNotAllowedException.class)
  public static ResponseEntity<BaseResponse> handlePostingNotAllowed() {
    ArrayList<String> codes = new ArrayList<>(List.of(ApiExceptionCode.POSTING_DISALLOWED));
    return new ResponseEntity<>(new ApiExceptionResponse(codes), HttpStatus.FORBIDDEN);
  }
}
