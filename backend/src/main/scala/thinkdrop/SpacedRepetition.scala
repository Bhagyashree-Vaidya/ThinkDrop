package thinkdrop

import java.time.LocalDate

/** SM-2 spaced-repetition algorithm (Wozniak 1987).
 *
 *  Quality ratings:
 *    5 — perfect recall, no hesitation
 *    4 — correct with slight hesitation
 *    3 — correct but required significant effort
 *    2 — incorrect; the correct answer felt familiar
 *    1 — incorrect; the correct answer was hard
 *    0 — complete blackout
 *
 *  Ratings 0–2 → failed review: reset interval to 1 day, keep EF.
 *  Ratings 3–5 → passed:        advance interval using EF, update EF.
 */
object SpacedRepetition:

  /** Recompute SM-2 fields after a single review. */
  def update(meta: EntryMeta, quality: Int, today: LocalDate): UpdatedMeta =
    val q = quality.clamp(0, 5)

    if q < 3 then
      // Failed — reset repetition count, try again tomorrow
      UpdatedMeta(
        id          = meta.id,
        easeFactor  = meta.easeFactor,   // EF is unchanged on failure
        interval    = 1,
        repetitions = 0,
        dueDate     = today.plusDays(1).toString,
      )
    else
      val newRepetitions = meta.repetitions + 1

      // Standard SM-2 interval schedule
      val newInterval = newRepetitions match
        case 1 => 1
        case 2 => 6
        case _ => math.round(meta.interval * meta.easeFactor).toInt.max(1)

      // EF adjustment: EF' = EF + (0.1 − (5−q)·(0.08 + (5−q)·0.02))
      val efDelta    = 0.1 - (5 - q) * (0.08 + (5 - q) * 0.02)
      val newEF      = (meta.easeFactor + efDelta).max(1.3)   // floor 1.3

      UpdatedMeta(
        id          = meta.id,
        easeFactor  = BigDecimal(newEF).setScale(4, BigDecimal.RoundingMode.HALF_UP).toDouble,
        interval    = newInterval,
        repetitions = newRepetitions,
        dueDate     = today.plusDays(newInterval).toString,
      )

  /** True when the entry is due for review on or before `today`. */
  def isDue(meta: EntryMeta, today: LocalDate): Boolean =
    !LocalDate.parse(meta.dueDate).isAfter(today)

  extension (n: Int)
    def clamp(lo: Int, hi: Int): Int = n.max(lo).min(hi)
