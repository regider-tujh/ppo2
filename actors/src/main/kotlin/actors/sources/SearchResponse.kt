package actors.sources

data class SearchResponse (
    val results: List<SearchResult>
)

data class SearchResult(
    val url: String,
    val sourceType: SourceType,
    val title: String,
    val description: String
)

enum class SourceType {
    FIRST_SOURCE,
    SECOND_SOURCE,
    THIRD_SOURCE
}