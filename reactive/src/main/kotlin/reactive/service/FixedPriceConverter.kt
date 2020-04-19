package reactive.service

import org.springframework.stereotype.Component
import reactive.model.Currency
import reactive.model.Price
import reactor.core.publisher.Mono

@Component
class FixedPriceConverter : PriceConverter {
    private val cource = mapOf(
        Currency.USD to mapOf(
            Currency.USD to 1.0,
            Currency.RUB to 66.0
        ),
        Currency.RUB to mapOf(
            Currency.USD to 1.0 / 66.0,
            Currency.RUB to 1.0
        )
    )

    override fun convert(price: Price, currency: Currency): Mono<Price> = Mono.just(
        Price(
            currency = currency,
            value = price.value * cource.getValue(price.currency).getValue(currency)
        )
    )
}