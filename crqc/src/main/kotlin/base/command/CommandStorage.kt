package base.command

import java.time.Instant
import java.util.*

interface CommandStorage {
    suspend fun renewalSubscription(userId: UUID, endDate: Instant)

    suspend fun enterGate(userId: UUID)

    suspend fun exitGate(userId: UUID)
}