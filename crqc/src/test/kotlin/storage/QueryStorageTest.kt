package storage

import base.command.MongoDbCommandStorage
import base.query.MongoQueryStorage
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

internal class QueryStorageTest {
    val database = MongodbReactiveClientBase().createMongoClient().getDatabase("test")
    @Test
    fun `should track user visit`(): Unit = runBlocking {
        val storage = MongoDbCommandStorage(database)
        val query = MongoQueryStorage(database)

        val userId = UUID.randomUUID()

        val enterTime = enterGateEvent(storage, userId)

        val user = query.getUser(userId)
        val visit = user!!.entrance[0].epochSecond

        assertTrue(visit >= enterTime)
    }

    @Test
    fun `should return all user visits`(): Unit = runBlocking {
        val storage = MongoDbCommandStorage(database)
        val query = MongoQueryStorage(database)

        val userId1 = UUID.randomUUID()
        val userId2 = UUID.randomUUID()

        val enterTime1 = enterGateEvent(storage, userId1)
        val enterTime2 = enterGateEvent(storage, userId2)

        val user1 = query.getUser(userId1)
        val user2 = query.getUser(userId2)

        val visit1 = user1!!.entrance[0].epochSecond
        assertTrue(visit1 >= enterTime1)

        val visit2 = user2!!.entrance[0].epochSecond
        assertTrue(visit2 >= enterTime2)
    }

    private suspend fun enterGateEvent(
        storage: MongoDbCommandStorage,
        userId: UUID
    ): Long {
        val enterTime = Instant.now().epochSecond
        storage.renewalSubscription(userId, Instant.now().plusMillis(10000))
        storage.enterGate(userId)
        return enterTime
    }
}