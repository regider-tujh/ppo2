package base.model

import java.time.Instant
import java.util.*

data class Visit(
    val userId: UUID,
    val start: Instant,
    val end: Instant
)