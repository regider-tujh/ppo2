package actors.sources

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mikael.urlbuilder.UrlBuilder
import java.net.ProxySelector
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLParameters

class FakeSource(
    private val sourceType: SourceType,
    private val sourceUrl: String
) : SearchSource {
    private val client = HttpClient.newBuilder()
        .proxy(ProxySelector.getDefault())
        .sslContext(SSLContext.getDefault())
        .sslParameters(SSLParameters())
        .build()

    override fun search(searchRequest: SearchRequest): SearchResponse {
        val url = UrlBuilder.fromString("$sourceUrl/search")
            .addParameter("query", searchRequest.request)
            .toUri()

        val httpRequest = HttpRequest.newBuilder(url)
            .GET()
            .timeout(DEFAULT_TIMEOUT)
            .build()

        val httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body()


        val searchResults = objectMapper.readValue(httpResponse, InnerSearchResponse::class.java).results

        return SearchResponse(
            results = searchResults.take(5).map {
                SearchResult(
                    url = it.url,
                    description = it.description,
                    sourceType = sourceType,
                    title = it.title
                )
            }
        )
    }

    private data class InnerSearchResponse(
        val results: List<InnerSearchResult>
    )

    private data class InnerSearchResult(
        val url: String,
        val title: String,
        val description: String
    )


    private companion object {
        val DEFAULT_TIMEOUT: Duration = Duration.ofSeconds(30)

        val objectMapper = jacksonObjectMapper()
    }
}