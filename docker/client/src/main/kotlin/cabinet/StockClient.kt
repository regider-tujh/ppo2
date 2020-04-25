package cabinet

import cabinet.model.Stock
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.future.await
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.util.*

data class CreateStock(
    val count: Int,
    val price: Int
)

data class CreateCompany(
    val name: String,
    val stock: CreateStock
)

data class Company(
    val id: UUID,
    val name: String
)

class StockClient(
    private val host: URI
) {
    private val objectMapper = jacksonObjectMapper().apply {
        findAndRegisterModules()
    }

    private val client = HttpClient.newHttpClient();

    suspend fun createCompany(company: CreateCompany): Company {
        val url = host.resolve("/company")

        val request = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(company)))
            .header("Content-Type", "application/json")
            .uri(url)
            .build()

        val response = client.sendAsync(request, BodyHandlers.ofString()).await().body()
        return objectMapper.readValue(response)
    }

    suspend fun getStock(companyId: UUID): Stock? {
        val url = host.resolve("/company/$companyId/stock")

        val request = HttpRequest.newBuilder()
            .GET()
            .uri(url)
            .build()

        val response = client.sendAsync(request, BodyHandlers.ofString()).await().body()
        return objectMapper.readValue(response)
    }

    suspend fun buyStock(companyId: UUID, count: Int): Stock {
        val url = host.resolve("/company/$companyId/stock/trade")

        val op = """
            {
            "type": "BUY",
            "count": $count
            }
        """.trimIndent()

        val request = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(op))
            .header("Content-Type", "application/json")
            .uri(url)
            .build()

        val response = client.sendAsync(request, BodyHandlers.ofString()).await().body()
        return objectMapper.readValue(response)
    }

    suspend fun sellStock(companyId: UUID, count: Int): Stock {
        val url = host.resolve("/company/$companyId/stock/trade")

        val op = """
            {
            "type": "SELL",
            "count": $count
            }
        """.trimIndent()

        val request = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(op))
            .header("Content-Type", "application/json")
            .uri(url)
            .build()

        val response = client.sendAsync(request, BodyHandlers.ofString()).await().body()
        return objectMapper.readValue(response)
    }
}