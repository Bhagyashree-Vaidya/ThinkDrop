package thinkdrop

import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto.*

// ─── Per-entry SM-2 metadata ──────────────────────────────────────────────────
// Stored in localStorage alongside each entry; sent to the backend for session
// generation and returned updated after each review.

case class EntryMeta(
  id:          String,
  kind:        String,        // word | concept | theory | quote | idea | observation
  title:       String,
  easeFactor:  Double = 2.5,  // SM-2 EF — starts at 2.5, floor 1.3
  interval:    Int    = 0,    // days until next review (0 = not yet reviewed)
  repetitions: Int    = 0,    // successful review count
  dueDate:     String,        // ISO-8601 date, e.g. "2026-05-01"
  addedDate:   String,        // ISO-8601 date
) derives Encoder.AsObject, Decoder

// ─── Session generation request ───────────────────────────────────────────────

case class SessionRequest(
  entries:   List[EntryMeta],
  maxNew:    Int = 3,         // max new (never-reviewed) items per session
  maxReview: Int = 7,         // max due-for-review items per session
  today:     String,          // ISO-8601 date — caller provides so TZ is correct
) derives Encoder.AsObject, Decoder

// ─── Session response ─────────────────────────────────────────────────────────

case class SessionItem(
  entry:       EntryMeta,
  sessionType: String,        // "new" | "review"
) derives Encoder.AsObject, Decoder

case class SessionResponse(
  items:       List[SessionItem],
  newCount:    Int,
  reviewCount: Int,
  sessionId:   String,
) derives Encoder.AsObject, Decoder

// ─── Review submission ────────────────────────────────────────────────────────
// The client sends back each reviewed item with its current SM-2 state and the
// quality score the user implicitly gave (based on how quickly / correctly they
// recalled it).  0–2 = failed, 3–5 = passed (5 = perfect).

case class ReviewResult(
  entryId:     String,
  quality:     Int,           // 0–5
  currentMeta: EntryMeta,     // full meta so the server can recompute SM-2
) derives Encoder.AsObject, Decoder

case class UpdatedMeta(
  id:          String,
  easeFactor:  Double,
  interval:    Int,
  repetitions: Int,
  dueDate:     String,
) derives Encoder.AsObject, Decoder

case class ReviewResponse(
  updated: List[UpdatedMeta],
) derives Encoder.AsObject, Decoder
