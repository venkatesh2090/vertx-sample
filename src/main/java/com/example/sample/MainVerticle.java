package com.example.sample;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.Objects;

public class MainVerticle extends AbstractVerticle {

  private final int port;

  public MainVerticle(int port) {
    this.port = port;
  }

  public MainVerticle() {
    this(Integer.parseInt(Objects.nonNull(System.getenv("PORT")) ?
        System.getenv("PORT") :
        "8080"
    ));
  }

  @Override
  public void start() {
    Router router = Router.router(vertx);

    router.get("/")
        .respond(ctx ->
          Future.succeededFuture(
            json()
              .put("welcome", ctx.request().connection().remoteAddress().toString())
          ));

    router.get("/hello")
      .respond(ctx ->
        Future.succeededFuture(
          json()
            .put("hello", "world")
        ));
    router.get("/hello/:name")
      .respond(ctx ->
        Future.succeededFuture(
          json()
            .put("hello", ctx.pathParam("name"))
        ));

    router.route()
        .respond(ctx ->
          Future.succeededFuture(
            json()
              .put("status", 404)
              .put("msg", "Page Not Found")
          ));

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(port)
      .onSuccess(server -> System.out.println("Server started on port " + server.actualPort()));
  }

  JsonObject json() {
    return new JsonObject();
  }

}
