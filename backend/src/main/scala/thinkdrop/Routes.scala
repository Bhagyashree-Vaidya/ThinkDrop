package thinkdrop

import cats.effect.IO
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.*
import io.circe.syntax.*

import java.time.LocalDate

object Routes:

  val api: HttpRoutes[IO] = HttpRoutes.of[IO]:

    // ── Health check ────────────────────────────────────────────────────────
    case GET -> Root / "health" =>
      Ok("""{"status":"ok","service":"thinkdrop"}""")

    // ── Generate a session ─────────────────────────────────────────────────
    // POST /api/session
    // Body: SessionRequest JSON
    // Returns: SessionResponse JSON
    case req @ POST -> Root / "api" / "session" =>
      for
        sessionReq <- req.as[SessionRequest]
        response    = SessionGenerator.generate(sessionReq)
        resp       <- Ok(response)
      yield resp

    // ── Submit review results and get updated SM-2 fields ─────────────────
    // POST /api/review
    // Body: List[ReviewResult] JSON  (each item carries its current EntryMeta)
    // Returns: ReviewResponse JSON   (updated fields to persist in localStorage)
    case req @ POST -> Root / "api" / "review" =>
      for
        results  <- req.as[List[ReviewResult]]
        today     = LocalDate.now()
        updated   = results.map(r => SpacedRepetition.update(r.currentMeta, r.quality, today))
        resp     <- Ok(ReviewResponse(updated))
      yield resp

    // ── Next due date preview (used by the streak-at-risk warning) ─────────
    // POST /api/next-due
    // Body: List[EntryMeta] JSON
    // Returns: {"nextDue": "2026-05-02"} or {"nextDue": null}
    case req @ POST -> Root / "api" / "next-due" =>
      for
        metas    <- req.as[List[EntryMeta]]
        today     = LocalDate.now()
        nextDue   = metas
                      .map(e => LocalDate.parse(e.dueDate))
                      .filter(!_.isBefore(today))
                      .minOption
                      .map(_.toString)
        body      = nextDue.fold("""{"nextDue":null}""")(d => s"""{"nextDue":"$d"}""")
        resp     <- Ok(body)
      yield resp
