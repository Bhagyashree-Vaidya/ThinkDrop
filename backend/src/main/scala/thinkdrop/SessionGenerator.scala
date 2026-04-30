package thinkdrop

import java.time.LocalDate
import java.util.UUID

/** Generates a ThinkDrop review session.
 *
 *  Strategy (mirrors Duolingo's session model):
 *    1. Split entries into "new" (repetitions == 0) and "existing".
 *    2. From existing, pick those due for review today, sorted most-overdue first.
 *    3. Cap both pools to the configured maxNew / maxReview limits.
 *    4. Interleave reviews and new items so the session stays varied.
 *    5. If there are no reviews, lead with new items and vice versa.
 */
object SessionGenerator:

  def generate(req: SessionRequest): SessionResponse =
    val today = LocalDate.parse(req.today)

    val (newEntries, existingEntries) =
      req.entries.partition(_.repetitions == 0)

    // Reviews: due on or before today, most-overdue first
    val reviewItems: List[SessionItem] =
      existingEntries
        .filter(SpacedRepetition.isDue(_, today))
        .sortBy(e => LocalDate.parse(e.dueDate))
        .take(req.maxReview)
        .map(SessionItem(_, "review"))

    // New items: in the order they were added (addedDate)
    val newItems: List[SessionItem] =
      newEntries
        .sortBy(_.addedDate)
        .take(req.maxNew)
        .map(SessionItem(_, "new"))

    val items = interleave(reviewItems, newItems)

    SessionResponse(
      items       = items,
      newCount    = newItems.length,
      reviewCount = reviewItems.length,
      sessionId   = UUID.randomUUID().toString,
    )

  // Interleave two lists: a₀ b₀ a₁ b₁ … trailing elements appended.
  private def interleave[A](as: List[A], bs: List[A]): List[A] =
    (as, bs) match
      case (Nil, _)             => bs
      case (_, Nil)             => as
      case (a :: at, b :: bt)   => a :: b :: interleave(at, bt)
