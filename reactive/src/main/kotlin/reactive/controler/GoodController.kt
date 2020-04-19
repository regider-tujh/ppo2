package reactive.controler

import org.bson.types.ObjectId
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactive.model.Good
import reactive.model.Price
import reactive.service.PriceConverter
import reactive.storage.GoodStorage
import reactive.storage.UserStorage
import reactor.core.publisher.Mono


@RestController
@RequestMapping(
    "/good",
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class GoodController(
    private val priceConverter: PriceConverter,
    private val users: UserStorage,
    private val goods: GoodStorage
) {
    @GetMapping("/{goodId}")
    fun getGoodById(
        @PathVariable goodId: String,
        @RequestParam("userId") userId: String
    ): Mono<Good> {
        return users.getUser(userId)
            .flatMap { user ->
                goods.getGood(goodId).flatMap { good ->
                    priceConverter.convert(good.price, user.currency).map { good.copy(price = it) }
                }
            }
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addGood(@RequestBody request: CreateGoodRequest): Mono<Good> {
        return Mono
            .just(request)
            .map { convertGood(it) }
            .log()
            .flatMap { goods.saveGood(it) }
    }

    data class CreateGoodRequest(
        val name: String,
        val price: Price
    )

    private fun convertGood(request: CreateGoodRequest): Good {
        return Good(
            goodId = ObjectId().toHexString(),
            name = request.name,
            price = request.price
        )
    }
}