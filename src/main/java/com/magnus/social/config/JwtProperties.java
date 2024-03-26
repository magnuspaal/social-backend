package com.magnus.social.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "application.jwt")
@Getter
@Setter
public class JwtProperties {
  @NotBlank
  private String secretKey;

  @ConstructorBinding
  public JwtProperties(String secretKey) {
    this.secretKey = secretKey;
  }
}