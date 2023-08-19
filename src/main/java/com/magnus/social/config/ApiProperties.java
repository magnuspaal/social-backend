package com.magnus.social.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "application.api")
@Getter
@Setter
public class ApiProperties {
  private String allowedOrigins;
  private String fileServerUrl;
  private String fileServerApiKey;

  public List<String> getAllowedOrigins() {
    return List.of(allowedOrigins.split(","));
  }
}