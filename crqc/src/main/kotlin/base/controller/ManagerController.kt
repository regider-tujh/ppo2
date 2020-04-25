package base.controller

import base.command.CommandStorage
import base.model.User
import base.query.QueryStorage
import base.utils.coroutineToMono
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

@RestController
@RequestMapping(
    "/manager",
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class ManagerController(
    val commandStorage: CommandStorage,
    val queryStorage: QueryStorage
) {
    data class SubscriptionTill(
        val till: Instant
    )

    @PostMapping("/{userId}/sub")
    fun subscription(
        @PathVariable userId: UUID,
        @RequestBody sub: SubscriptionTill
    ): Mono<ResponseEntity<Unit>> = coroutineToMono {
        commandStorage.renewalSubscription(userId, sub.till)
        ResponseEntity.ok(Unit)
    }

    @GetMapping("/{userId}")
    fun findUser(
        @PathVariable userId: UUID
    ): Mono<ResponseEntity<User>> = coroutineToMono {
        val user = queryStorage.getUser(userId)
        if (user == null) ResponseEntity.notFound().build() else ResponseEntity.ok(user)
    }
}