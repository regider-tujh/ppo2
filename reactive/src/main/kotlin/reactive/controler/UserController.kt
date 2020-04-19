package reactive.controler

import org.bson.types.ObjectId
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactive.model.Currency
import reactive.model.User
import reactive.storage.UserStorage
import reactor.core.publisher.Mono


@RestController
@RequestMapping(
    "/user",
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class UserController(
    private val users: UserStorage
) {

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createUser(@RequestBody request: CreateUser): Mono<User> {
        return Mono
            .just(request)
            .map { convertUser(it) }
            .log()
            .flatMap { users.saveUser(it) }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: String): Mono<User> {
        return users.getUser(userId)
    }

    data class CreateUser(
        val name: String,
        val currency: Currency
    )

    private fun convertUser(it: CreateUser): User {
        return User(
            userId = ObjectId().toHexString(),
            name = it.name,
            currency = it.currency
        )
    }
}