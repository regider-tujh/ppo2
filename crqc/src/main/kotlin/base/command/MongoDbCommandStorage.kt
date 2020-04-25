package base.command

import base.utils.jacksonCollection
import com.mongodb.client.model.Filters
import com.mongodb.reactivestreams.client.MongoDatabase
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.extra.math.max
import java.time.Instant
import java.util.*

@Component
class MongoDbCommandStorage(database: MongoDatabase) : CommandStorage {
    private val gateCommands = database.jacksonCollection<GateCommand>("gateCommands")
    private val subscriptionCommands = database.jacksonCollection<SubscriptionCommand>("gateCommands")
    override suspend fun renewalSubscription(userId: UUID, endDate: Instant) {
        val command = SubscriptionCommand(userId, endDate)

        subscriptionCommands.insertOne(command).awaitFirst()
    }

    override suspend fun enterGate(userId: UUID) {
        val filter = Filters.eq("userId", userId)
        val endDate = subscriptionCommands.find(filter).toFlux().max { left, right ->
            left.eventId.timestamp.compareTo(right.eventId.timestamp)
        }.awaitFirst().endDate

        if (Instant.now() < endDate) {
            gateCommands.insertOne(EnterCommand(userId))
        } else {
            throw IllegalStateException("Subscription expired")
        }

    }

    override suspend fun exitGate(userId: UUID) {
        val command = ExitCommand(userId)

        gateCommands.insertOne(command).awaitFirst()
    }
}