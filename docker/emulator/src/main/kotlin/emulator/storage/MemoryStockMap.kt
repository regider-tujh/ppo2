package emulator.storage


import java.util.*
import java.util.concurrent.ConcurrentHashMap

class MemoryStockMap : StockMap {
    private val companys: MutableMap<UUID, Company> = ConcurrentHashMap()
    private val stocks: MutableMap<UUID, Stock> = ConcurrentHashMap()

    override suspend fun addCompany(company: Company) {
        companys[company.id] = company
    }

    override suspend fun addStock(stock: Stock) {
        stocks[stock.companyId] = stock
    }

    override suspend fun buyStock(stockId: UUID, count: Int): Int {
        val stock = stocks.getValue(stockId)
        stocks[stockId] = stock.copy(count = stock.count - count)
        return stock.price
    }

    override suspend fun sellStock(stockId: UUID, count: Int): Int {
        val stock = stocks.getValue(stockId)
        stocks[stockId] = stock.copy(count = stock.count + count)
        return stock.price
    }

    override suspend fun updatePrice(stockId: UUID, price: Int) {
        val stock = stocks.getValue(stockId)
        stocks[stockId] = stock.copy(price = price)
    }

    override suspend fun getStock(stockId: UUID): Stock? {
        return stocks[stockId]
    }

    override suspend fun getCompany(companyId: UUID): Company? {
        return companys[companyId]
    }
}