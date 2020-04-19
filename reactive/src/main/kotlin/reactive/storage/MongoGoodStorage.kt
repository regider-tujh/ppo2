package reactive.storage

import com.mongodb.client.model.Filters
import com.mongodb.reactivestreams.client.MongoDatabase
import org.springframework.stereotype.Component
import reactive.model.Good
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class MongoGoodStorage(database: MongoDatabase) : GoodStorage {
    private val collection = database.jacksonCollection<Good>(COLLECTION_NAME)


    override fun saveGood(good: Good): Mono<Good> {
        return collection.insertOne(good).toMono().thenReturn(good)
    }

    override fun getGood(goodId: String): Mono<Good> {
        return collection.find(Filters.eq("_id", goodId)).toMono()
    }

    private companion object {
        val COLLECTION_NAME = "goods"
    }
}