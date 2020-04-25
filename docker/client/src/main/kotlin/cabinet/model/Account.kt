package cabinet.model

import java.util.*

data class Account(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val balance: Int = 0,
    val stocks: List<Stock> = emptyList()
)