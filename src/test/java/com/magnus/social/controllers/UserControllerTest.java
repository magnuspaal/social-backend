package com.magnus.social.controllers;

import com.magnus.social.SocialApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SocialApplication.class)
@TestPropertySource(
    locations = "classpath:application-test.properties")
@ContextConfiguration(initializers = {UserControllerTest.Initializer.class})
@AutoConfigureMockMvc
@Testcontainers
public class UserControllerTest {

  @Container
  public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
      .withDatabaseName("user-controller-test")
      .withUsername("username")
      .withPassword("password");

  static class Initializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of(
          "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
          "spring.datasource.username=" + postgreSQLContainer.getUsername(),
          "spring.datasource.password=" + postgreSQLContainer.getPassword()
      ).applyTo(configurableApplicationContext.getEnvironment());
    }
  }

  @Autowired
  private MockMvc mvc;

  @Test
  public void Given_ValidTokenButUserNotInDB_WhenUserMeCalled_Then_ShouldCreateUserAndReturnUser() throws Exception {
    mvc.perform(get("/api/v1/user/me")
          .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJmaXJzdF9uYW1lIjoiSm9obiIsImlkIjo3LCJyb2xlIjoiVVNFUiIsInVzZXJuYW1lIjoiam9obi5kb2UiLCJsYXN0X25hbWUiOiJEb2UiLCJzdWIiOiJqb2huLmRvZUBlbWFpbC5jb20iLCJpYXQiOjE3MDY2MjM1NTQsImV4cCI6MzI1MDYyMjk0ODR9.9cDSj9ERrse3CLiqg7xOeczY-lrlYM2rOb0kztUBVhQ"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("7"))
        .andExpect(jsonPath("$.username").value("john.doe"))
        .andExpect(jsonPath("$.email").value("john.doe@email.com"));
  }
}
