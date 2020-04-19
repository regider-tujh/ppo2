package reactive.storage

import com.mongodb.client.model.Filters
import com.mongodb.reactivestreams.client.MongoDatabase
import org.springframework.stereotype.Component
import reactive.model.User
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class MongoUserStorage(database: MongoDatabase) : UserStorage {
    private val collection = database.jacksonCollection<User>(COLLECTION_NAME)


    override fun saveUser(user: User): Mono<User> {
        return collection.insertOne(user).toMono().thenReturn(user)
    }

    override fun getUser(userId: String): Mono<User> {
        return collection.find(Filters.eq("_id", userId)).toMono()
    }

    private companion object {
        const val COLLECTION_NAME = "users"
    }
}