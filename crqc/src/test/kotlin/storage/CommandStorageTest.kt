package storage

import base.command.MongoDbCommandStorage
import base.query.MongoQueryStorage
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import storage.MongodbReactiveClientBase
import java.time.Instant
import java.util.*

class CommandStorageTest  {
    val database = MongodbReactiveClientBase().createMongoClient().getDatabase("test")

    @Test
    fun `should create subscription if user not found`(): Unit = runBlocking {
        val storage = MongoDbCommandStorage(database)
        val query = MongoQueryStorage(database)

        val userId = UUID.randomUUID()
        val endDate = Instant.now()

        storage.renewalSubscription(userId, endDate)

        val user = query.getUser(userId)

        assertNotNull(user)
        assertEquals(userId, user!!.userId)
        assertEquals(endDate.epochSecond, user.endSubscription.epochSecond)
    }

    @Test
    fun `should renewal subscription`(): Unit = runBlocking {
        val storage = MongoDbCommandStorage(database)
        val query = MongoQueryStorage(database)

        val userId = UUID.randomUUID()
        val endDate = Instant.now().plusMillis(10000)

        storage.renewalSubscription(userId, Instant.now())
        storage.renewalSubscription(userId, endDate)

        val user = query.getUser(userId)

        assertNotNull(user)
        assertEquals(userId, user!!.userId)
        assertEquals(endDate.epochSecond, user.endSubscription.epochSecond)
    }

    @Test
    fun `should enter in gate`(): Unit = runBlocking {
        val storage = MongoDbCommandStorage(database)
        val query = MongoQueryStorage(database)

        val userId = UUID.randomUUID()

        val enterTime = Instant.now().epochSecond
        storage.renewalSubscription(userId, Instant.now().plusMillis(10000))
        storage.enterGate(userId)

        val user = query.getUser(userId)
        val visit = user!!.entrance[0].epochSecond

        assertTrue(visit >= enterTime)
    }

    @Test
    fun `should exit from gate`(): Unit = runBlocking {
        val storage = MongoDbCommandStorage(database)

        val userId = UUID.randomUUID()

        storage.renewalSubscription(userId, Instant.now().plusMillis(10000))

        storage.enterGate(userId)
        storage.exitGate(userId)
    }

}