package base.query

interface QueryProcessor<T: Query, R> {
    suspend fun aggregate(query: T): R
}