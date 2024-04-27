package com.magnus.social.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;

@ConfigurationProperties(prefix = "application.api")
@Getter
public class ApiProperties {
  @NotBlank
  private final List<String> allowedOrigins;

  @NotBlank
  private final String fileServerUrl;

  @ConstructorBinding
  public ApiProperties(List<String> allowedOrigins, String fileServerUrl, String fileServerApiKey) {
    this.allowedOrigins = allowedOrigins;
    this.fileServerUrl = fileServerUrl;
  }
}