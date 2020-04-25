package cabinet

import cabinet.model.Account
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.testcontainers.containers.FixedHostPortGenericContainer
import org.testcontainers.containers.GenericContainer
import java.net.URI

class KFixedHostPortGenericContainer(imageName: String) :
    FixedHostPortGenericContainer<KFixedHostPortGenericContainer>(imageName)

class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)


class CabinetTest {
    private val stockEmulator = KFixedHostPortGenericContainer("emulator:1.0")
        .withFixedExposedPort(8080, 8080)
        .withExposedPorts(8080)


    @Test
    fun `integration test`() {
        runBlocking {
            stockEmulator.start()
            val client = StockClient(URI.create("http://localhost:8080"))

            val cabinet = Cabinet(client)


            val company = client.createCompany(CreateCompany("One", CreateStock(12, 900)))

            val account = cabinet.addAccount(
                Account(
                    name = "Ivan",
                    balance = 40000
                )
            )

            cabinet.changeBalance(account.id, -10000)

            Assertions.assertEquals(30000, cabinet.getAccount(account.id)?.balance)

            cabinet.buyStock(account.id, company.id, 2)

            Assertions.assertTrue(cabinet.getAccount(account.id)!!.balance < 30000)

            val stocs = cabinet.getStock(account.id)!!

            Assertions.assertEquals(1, stocs.size)
            Assertions.assertEquals(2, stocs.first().count)

            Assertions.assertEquals(10, client.getStock(company.id)!!.count)

            println(cabinet.totalAmount(account.id))
        }
    }
}