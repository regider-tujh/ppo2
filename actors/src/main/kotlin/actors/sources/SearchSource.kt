package actors.sources

interface SearchSource {
    fun search(searchRequest: SearchRequest): SearchResponse
}