package reactive.storage

import reactive.model.Good
import reactor.core.publisher.Mono

interface GoodStorage {
    fun saveGood(good: Good): Mono<Good>

    fun getGood(goodId: String): Mono<Good>
}