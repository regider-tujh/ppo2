package cabinet.model

import java.util.*

data class Stock(
    val companyId: UUID,
    val count: Int,
    val price: Int = 0
)
