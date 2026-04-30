package thinkdrop

import cats.effect.*
import com.comcast.ip4s.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.{CORS, Logger}
import org.http4s.server.middleware.CORSPolicy

object Main extends IOApp.Simple:

  def run: IO[Unit] =
    val corsPolicy: CORSPolicy =
      CORS.policy
        .withAllowOriginAll          // allow the Vite dev server (localhost:5173)
        .withAllowMethodsAll
        .withAllowHeadersAll

    val routes = corsPolicy(Routes.api)
    val app    = Logger.httpApp(logHeaders = true, logBody = false)(routes.orNotFound)

    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(app)
      .build
      .use: server =>
        IO.println(s"ThinkDrop backend listening on ${server.address}") >>
        IO.never
