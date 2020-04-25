package storage

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.testcontainers.containers.GenericContainer

class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)

open class MongodbReactiveClientBase  {

    companion object {
        const val MONGODB_EXPOSED_PORT = 27017
        const val MONGODB_USERNAME = "test"
        const val MONGODB_PASSWORD = "test"

        @JvmStatic
        val mongo: KGenericContainer = KGenericContainer(
            "mongo:3.6.7"
        )
            .withExposedPorts(MONGODB_EXPOSED_PORT)
            .withEnv("MONGO_INITDB_ROOT_USERNAME",
                MONGODB_USERNAME
            )
            .withEnv("MONGO_INITDB_ROOT_PASSWORD",
                MONGODB_PASSWORD
            )

        init {
            mongo.start()
        }
    }

    fun createMongoClient(): MongoClient {
        val connectionString =
            "mongodb://$MONGODB_USERNAME:$MONGODB_PASSWORD@${mongo.containerIpAddress}:${mongo.getMappedPort(
                MONGODB_EXPOSED_PORT
            )}"

        return MongoClients.create(connectionString)
    }
}
