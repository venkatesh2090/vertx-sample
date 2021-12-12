package com.example.sample;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  private static final String HOST = "localhost";
  private static final int PORT = 8080;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(8080), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) {
    testContext.completeNow();
  }

  @Test
  void when_hello_then_return_world(Vertx vertx, VertxTestContext testContext) {
    var client = vertx.createHttpClient();

    client.request(HttpMethod.GET, PORT, HOST, "/hello")
        .compose(req -> req.send().compose(HttpClientResponse::body))
        .onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
          assertThat(buffer.toJsonObject().getString("hello")).isEqualTo("world");
          testContext.completeNow();
        })));
  }

  @ParameterizedTest
  @ValueSource(strings = {"name1", "venkatesh", "kannan"})
  void when_hello_with_name_then_return_name(String name, Vertx vertx, VertxTestContext testContext) {
    var client = vertx.createHttpClient();

    client.request(HttpMethod.GET, PORT, HOST, "/hello/" + name)
      .compose(req -> req.send().compose(HttpClientResponse::body))
      .onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
        assertThat(buffer.toJsonObject().getString("hello")).isEqualTo(name);
        testContext.completeNow();
      })));
  }

}
