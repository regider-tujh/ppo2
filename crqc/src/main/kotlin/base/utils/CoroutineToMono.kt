package base.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.reactor.mono
import reactor.core.publisher.Mono
import kotlin.coroutines.EmptyCoroutineContext

fun <T> coroutineToMono(func: suspend CoroutineScope.() -> T?): Mono<T> {
    return Mono.subscriberContext().flatMap { ctx ->
        mono(EmptyCoroutineContext, func)
    }
}
