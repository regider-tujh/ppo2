package cabinet

import cabinet.model.Account
import cabinet.model.Stock
import java.util.*
import java.util.concurrent.ConcurrentHashMap


class Cabinet(private val client: StockClient) {
    val accounts = ConcurrentHashMap<UUID, Account>()

    fun addAccount(account: Account): Account {
        accounts[account.id] = account
        return account
    }

    fun getAccount(accountId: UUID): Account? = accounts[accountId]

    fun changeBalance(accountId: UUID, sum: Int) {
        val account = accounts.getValue(accountId)
        accounts[account.id] = account.copy(
            balance = sum + account.balance
        )
    }

    suspend fun buyStock(accountId: UUID, companyId: UUID, amount: Int) {
        val account = accounts.getValue(accountId)
        val stocks = requireNotNull(client.getStock(companyId))

        require(stocks.count >= amount)
        val price = stocks.price * stocks.count

        require(price <= account.balance)
        client.buyStock(companyId, amount)
        accounts[accountId] = account.copy(
            balance = account.balance - price,
            stocks = account.stocks + stocks.copy(count = amount)
        )
    }

    suspend fun getStock(accountId: UUID): List<Stock>? {
        return accounts[accountId]?.stocks?.map {
            it.copy(price = client.getStock(it.companyId)?.price ?: 0)
        }
    }

    suspend fun totalAmount(accountId: UUID): Int? = getStock(accountId)?.sumBy { it.price * it.count }

}