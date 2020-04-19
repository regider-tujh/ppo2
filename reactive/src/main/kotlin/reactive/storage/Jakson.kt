package reactive.storage

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mongodb.reactivestreams.client.MongoCollection
import com.mongodb.reactivestreams.client.MongoDatabase
import org.mongojack.JacksonCodecRegistry

fun repositoryJackson(): ObjectMapper = jacksonObjectMapper()
    .findAndRegisterModules()
    .registerModule(JavaTimeModule())
    .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)


inline fun <reified T> MongoDatabase.jacksonCollection(collectionName: String): MongoCollection<T> {
    val jacksonCodecRegistry = JacksonCodecRegistry(repositoryJackson())
    jacksonCodecRegistry.addCodecForClass(T::class.java)
    return getCollection(collectionName)
        .withDocumentClass(T::class.java)
        .withCodecRegistry(jacksonCodecRegistry)
}
