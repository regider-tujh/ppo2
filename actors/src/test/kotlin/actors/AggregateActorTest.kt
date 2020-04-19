package actors

import actors.sources.FakeSource
import actors.sources.SearchRequest
import actors.sources.SearchResponse
import actors.sources.SourceType
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.Patterns
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import scala.concurrent.Await
import java.time.Duration


class AggregateActorTest {

    @Test
    fun `should merge result`() {
        val actors = ActorSystem.create("aggregator")
        val aggregation = Props.create(AggregateActor::class.java, Duration.ofSeconds(30), services)

        val actor = actors.actorOf(aggregation, "search")
        val request = SearchRequest("pig")

        val searchResponse = getSearchResponse(actor, request)

        Assertions.assertEquals(15, searchResponse.results.size)
    }

    @Test
    fun `timeout response`() {
        val actors = ActorSystem.create("aggregator")
        val aggregation = Props.create(AggregateActor::class.java, Duration.ofSeconds(3), services)

        val actor = actors.actorOf(aggregation, "search")
        val request = SearchRequest("pigs")

        val searchResponse = getSearchResponse(actor, request)

        Assertions.assertTrue(searchResponse.results.isEmpty())
    }

    private fun getSearchResponse(actor: ActorRef, request: SearchRequest): SearchResponse {
        return Await
            .result(
                Patterns
                    .ask(actor, request, Duration.ofSeconds(30).toMillis()),
                scala.concurrent.duration.Duration.Inf()
            ) as SearchResponse
    }

    private companion object {

        val stab = StabFakeSource(8090)
        val host = "http://localhost:8090"

        val services = listOf(
            FakeSource(SourceType.FIRST_SOURCE, host),
            FakeSource(SourceType.SECOND_SOURCE, host),
            FakeSource(SourceType.THIRD_SOURCE, host)
        )

        @BeforeAll
        @JvmStatic
        fun setup() {
            stab.start()
        }
    }
}
