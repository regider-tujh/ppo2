package base.controller

import base.service.Statistics
import base.utils.coroutineToMono
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping(
    "/statistic",
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class StatisticController(
    private val statistics: Statistics
) {
    @GetMapping("/visitsPerDay")
    fun visitsPerDay(
        @PathVariable userId: UUID
    ): Mono<ResponseEntity<Map<LocalDate, Int>>> = coroutineToMono {
        ResponseEntity.ok(statistics.getVisitsPerDay())
    }

    @GetMapping("/averageVisitTime")
    fun exit(
        @PathVariable userId: UUID
    ): Mono<ResponseEntity<Double>> = coroutineToMono {
        ResponseEntity.ok(statistics.getAverageVisitTime())
    }
}