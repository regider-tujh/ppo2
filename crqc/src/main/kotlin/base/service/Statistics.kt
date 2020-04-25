package base.service

import base.model.Visit
import base.query.QueryStorage
import kotlinx.coroutines.*
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Component
class Statistics(
    private val queryStorage: QueryStorage
) {
    private val visits: MutableList<Visit>
    private var lastTimestamp = Instant.EPOCH
    private val job: Job

    init {
        visits = runBlocking { queryStorage.getVisits(lastTimestamp).toMutableList() }
        lastTimestamp = visits.maxBy { it.end }?.end ?: Instant.now()

        job = GlobalScope.launch {
            while (true) {
                val newVisits = queryStorage.getVisits(lastTimestamp).toMutableList()
                visits.addAll(newVisits)
                lastTimestamp = newVisits.maxBy { it.start }?.start ?: Instant.now()

                delay(5000)
            }
        }
    }

    fun getVisitsPerDay(): Map<LocalDate, Int> {
        return visits.groupBy { LocalDate.ofInstant(it.start, ZoneId.systemDefault()) }.mapValues { it.value.size }
    }

    fun getAverageVisitTime(): Double {
        return visits.map { Duration.between(it.start, it.end).toSeconds() }.average()
    }
}