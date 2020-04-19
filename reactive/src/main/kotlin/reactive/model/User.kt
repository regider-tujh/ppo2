package reactive.model

import com.fasterxml.jackson.annotation.JsonProperty


data class User(
    @JsonProperty("_id")
    val userId: String,
    val name: String,
    val currency: Currency
)

enum class Currency {
    RUB,
    USD
}
