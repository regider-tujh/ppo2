package emulator

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import emulator.storage.Company
import emulator.storage.MemoryStockMap
import emulator.storage.Stock
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.random.Random

data class TradeOperation(
    val type: Operation,
    val count: Int
)

enum class Operation {
    BUY,
    SELL
}

data class CreateStock(
    val count: Int,
    val price: Int
)

data class CreateCompany(
    val name: String,
    val stock: CreateStock
)


fun main() = runBlocking {
    val stockMap = MemoryStockMap()

    val server = embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            jackson {
                findAndRegisterModules()
                registerModule(KotlinModule())
                registerModule(JavaTimeModule())
            }
        }

        routing {
            post("/company") {
                val request = call.receive<CreateCompany>()
                val company = Company(UUID.randomUUID(), request.name)

                stockMap.addCompany(company)

                val stock = Stock(
                    companyId = company.id,
                    count = request.stock.count,
                    price = request.stock.price
                )

                stockMap.addStock(stock)

                call.respond(company)
            }

            get("/company/{companyId}") {
                val companyId = call.parameters["companyId"]!!.let(UUID::fromString)
                val company = stockMap.getCompany(companyId)
                if (company == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(company)
                }
            }

            get("/company/{companyId}/stock") {
                val companyId = call.parameters["companyId"]!!.let(UUID::fromString)

                val stock = stockMap.getStock(companyId)
                if (stock != null) {
                    val newPrice = stock.price + Random.nextInt(-50, 50)
                    stockMap.updatePrice(companyId, newPrice)
                    call.respond(stock.copy(price = newPrice))
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            post("/company/{companyId}/stock/trade") {
                val companyId = call.parameters["companyId"]!!.let(UUID::fromString)

                val op = call.receive<TradeOperation>()

                val stock = stockMap.getStock(companyId)
                if (stock != null) {
                    if (stock.count < op.count) {
                        call.respond("Not enough stocks")
                        return@post
                    }
                    val count: Int
                    val price = if (op.type == Operation.BUY) {
                        count = -op.count
                        stockMap.buyStock(companyId, op.count)
                    } else {
                        count = op.count
                        stockMap.sellStock(companyId, op.count)
                    }
                    call.respond(stock.copy(price = price, count = stock.count + count))
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }.start(wait = true)
}