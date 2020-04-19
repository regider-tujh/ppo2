package reactive.configuration

import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoDatabase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration {

    @Bean
    fun mongoDatabase(): MongoDatabase {
        val client = MongoClients.create()
        return client.getDatabase("test")
    }
}