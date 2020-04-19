package actors

import actors.sources.FakeSource
import actors.sources.SearchRequest
import actors.sources.SourceType
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.Patterns
import java.time.Duration

fun main() {
    StabFakeSource(8090).start()
    val host = "http://localhost:8090"
    val source = listOf(
        FakeSource(SourceType.FIRST_SOURCE, host),
        FakeSource(SourceType.SECOND_SOURCE, host),
        FakeSource(SourceType.THIRD_SOURCE, host)
    )

    val actors = ActorSystem.create("aggregator")
    val aggregation = Props.create(AggregateActor::class.java, Duration.ofSeconds(30), source)

    val actor = actors.actorOf(aggregation, "search")
    val request = SearchRequest("pig")

    Patterns.ask(actor, request, Duration.ofSeconds(30).toMillis()).onComplete({ response ->
        println("Received result")
        println(response)
    }, actors.dispatcher())
}