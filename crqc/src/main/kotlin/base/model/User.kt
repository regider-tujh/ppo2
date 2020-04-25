package base.model

import java.time.Instant
import java.util.*

data class User(
    val userId: UUID,
    val endSubscription: Instant,
    val entrance: List<Instant>
)