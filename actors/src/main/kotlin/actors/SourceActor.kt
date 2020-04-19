package actors

import actors.sources.SearchRequest
import actors.sources.SearchSource
import akka.actor.AbstractActor

class SourceActor(
    private val source: SearchSource
) : AbstractActor() {
    override fun createReceive(): AbstractActor.Receive =
        receiveBuilder().match(SearchRequest::class.java) {
            val result = source.search(it)
            sender.forward(result, context)
        }.build()
}