package base.query

import base.model.User
import base.model.Visit
import java.time.Instant
import java.util.*

interface QueryStorage {
    suspend fun getUser(userId: UUID): User?

    suspend fun getVisits(since: Instant): List<Visit>
}