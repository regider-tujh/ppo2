package base.query

import java.time.Instant
import java.util.*

sealed class Query

data class GetUser(val userId: UUID): Query()

class GetVisits(val from: Instant): Query()
