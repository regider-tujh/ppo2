package base.command

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.bson.types.ObjectId
import java.time.Instant
import java.util.*

sealed class Command {
    abstract val userId: UUID
    abstract val eventId: ObjectId
}

data class SubscriptionCommand(
    override val userId: UUID,
    val endDate: Instant,
    @field:JsonProperty("_id")
    override val eventId: ObjectId = ObjectId.get()
) : Command()

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
open class GateCommand(
    override val userId: UUID,
    val eventType: GateEventType,
    @field:JsonProperty("_id")
    override val eventId: ObjectId = ObjectId.get()
) : Command()


enum class GateEventType(val type: String) {
    ENTER("ENTER"),
    EXIT("EXIT");
}

class EnterCommand(
    userId: UUID,
    eventId: ObjectId = ObjectId.get()
) : GateCommand(userId, GateEventType.ENTER, eventId)

class ExitCommand(
    userId: UUID,
    eventId: ObjectId = ObjectId.get()
) : GateCommand(userId, GateEventType.EXIT, eventId)

