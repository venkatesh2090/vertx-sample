package com.example.sample;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
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
  void when_home_then_respond_with_header(Vertx vertx, VertxTestContext testContext) {
    var client = vertx.createHttpClient();
    var testHeader = "X-Test-Header";
    var testValue = "Test Value";

    var options = new RequestOptions();
    options.addHeader(testHeader, testValue);
    options.setMethod(HttpMethod.GET);
    options.setHost(HOST);
    options.setPort(PORT);
    options.setURI("/");
    client.request(options)
      .compose(req -> req.send().compose(HttpClientResponse::body))
      .onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
        var headersInResponse = buffer.toJsonObject().getJsonObject("request").getJsonObject("headers");
        assertThat(headersInResponse.containsKey(testHeader)).isTrue();
        assertThat(headersInResponse.getString(testHeader)).isEqualToIgnoringCase(testValue);
        testContext.completeNow();
      })));
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
