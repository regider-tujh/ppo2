package reactive.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Good(
    @JsonProperty("_id")
    val goodId: String,
    val name: String,
    val price: Price
)

data class Price(
    val value: Double,
    val currency: Currency
)