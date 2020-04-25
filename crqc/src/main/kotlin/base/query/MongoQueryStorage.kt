package base.query

import base.command.GateCommand
import base.command.GateEventType
import base.command.SubscriptionCommand
import base.model.User
import base.model.Visit
import base.utils.jacksonCollection
import com.mongodb.client.model.Filters
import com.mongodb.reactivestreams.client.MongoDatabase
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.bson.types.ObjectId
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.extra.math.max
import java.time.Instant
import java.util.*

@Component
class MongoQueryStorage(database: MongoDatabase) : QueryStorage {
    private val gateCommands = database.jacksonCollection<GateCommand>("gateCommands")
    private val subscriptionCommands = database.jacksonCollection<SubscriptionCommand>("gateCommands")

    override suspend fun getUser(userId: UUID): User? {
        val filter = Filters.eq("userId", userId)
        val endDate = subscriptionCommands.find(filter).toFlux().max { left, right ->
            left.eventId.timestamp.compareTo(right.eventId.timestamp)
        }.awaitFirstOrNull()?.endDate ?: return null

        val visitsFilter = Filters.and(Filters.eq("userId", userId), Filters.eq("eventType", GateEventType.ENTER))

        val ent = gateCommands.find(visitsFilter).toFlux().map { it.eventId.date.toInstant() }.asFlow().toList()

        return User(userId, endDate, ent)
    }

    override suspend fun getVisits(since: Instant): List<Visit> {
        val id = ObjectId(Date.from(since))

        return gateCommands.find(Filters.gt("_id", id)).asFlow().toList().groupBy { it.userId }
            .flatMap { (userId, commands) ->
                val result = mutableListOf<Visit>()
                for (i in 0 until commands.size - 1 step 2) {
                    val enter = commands[i].eventId.date.toInstant()
                    val exit = commands[i + 1].eventId.date.toInstant()
                    result.add(Visit(userId, enter, exit))
                }
                result
            }

    }
}