package com.example.sample;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

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
      .listen(8080)
      .onSuccess(server -> System.out.println("Server started on port " + server.actualPort()));
  }

  JsonObject json() {
    return new JsonObject();
  }

}
