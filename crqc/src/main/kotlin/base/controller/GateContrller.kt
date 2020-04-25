package base.controller

import base.command.CommandStorage
import base.utils.coroutineToMono
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping(
    "/gate",
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class GateContrller(
    val commandStorage: CommandStorage
) {

    @GetMapping("/{userId}")
    fun enter(
        @PathVariable userId: UUID
    ): Mono<ResponseEntity<Unit>> = coroutineToMono {
        commandStorage.enterGate(userId)
        ResponseEntity.ok(Unit)
    }

    @GetMapping("/{userId}")
    fun exit(
        @PathVariable userId: UUID
    ): Mono<ResponseEntity<Unit>> = coroutineToMono {
        commandStorage.exitGate(userId)
        ResponseEntity.ok(Unit)
    }
}