package reactive.storage

import reactive.model.User
import reactor.core.publisher.Mono

interface UserStorage {
    fun saveUser(user: User): Mono<User>

    fun getUser(userId: String): Mono<User>
}