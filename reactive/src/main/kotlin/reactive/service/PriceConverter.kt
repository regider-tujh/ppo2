package reactive.service

import reactive.model.Currency
import reactive.model.Price
import reactor.core.publisher.Mono

interface PriceConverter {
    fun convert(price: Price, currency: Currency): Mono<Price>
}