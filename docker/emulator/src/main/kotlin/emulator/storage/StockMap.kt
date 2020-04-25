package emulator.storage

import java.util.*

data class Stock(
    val companyId: UUID,
    val count: Int,
    val price: Int
)

data class Company(
    val id: UUID,
    val name: String
)

interface StockMap {
    suspend fun addCompany(company: Company)

    suspend fun addStock(stock: Stock)

    suspend fun buyStock(stockId: UUID, count: Int): Int

    suspend fun sellStock(stockId: UUID, count: Int): Int

    suspend fun updatePrice(stockId: UUID, price: Int)

    suspend fun getStock(stockId: UUID): Stock?

    suspend fun getCompany(companyId: UUID): Company?
}