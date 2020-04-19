package actors

import actors.sources.SearchRequest
import actors.sources.SearchResponse
import actors.sources.SearchSource
import akka.actor.AbstractActorWithTimers
import akka.actor.ActorRef
import akka.actor.Props.create
import java.time.Duration

class AggregateActor(
    private val searchTimeout: Duration,
    private val sources: List<SearchSource>
) : AbstractActorWithTimers(),
    AutoCloseable {

    private val children: MutableList<ActorRef> = mutableListOf()
    private var got: Int = 0
    private var responseSent = false
    private var answerDestination: ActorRef? = null

    private var response = SearchResponse(emptyList())

    override fun preStart() {
        val system = context.system
        sources.forEach { source ->
            children.add(system.actorOf(create(SourceActor::class.java, source)))
        }
    }

    override fun createReceive(): Receive {
        return receiveBuilder()
            .match(SearchRequest::class.java) { req ->
                got = children.size
                answerDestination = sender
                children.forEach { child -> child.tell(req, self) }
                timers.startSingleTimer("TL", Timeout, searchTimeout)
            }
            .match(SearchResponse::class.java) { resp ->
                response = SearchResponse(response.results + resp.results)
                got--
                if (got <= 0) {
                    sendResponse()
                }
            }
            .match(Timeout::class.java) { sendResponse() }
            .matchAny {
                System.err.println("Unknown message")
                System.err.flush()
            }
            .build()
    }

    private fun sendResponse() {
        if (!responseSent) {
            answerDestination?.forward(response, context)
            responseSent = true
            close()
        }
    }

    override fun close() {
        context.children.forEach(context::stop)
        context.stop(self)
    }

    private object Timeout
}