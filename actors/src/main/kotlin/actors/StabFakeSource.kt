package actors

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock

import java.time.Duration

class StabFakeSource(private val port: Int) {
    private val json = """
        {
          "results": [
            {
              "title": "Pig",
              "description": "Pig!",
              "url": "www.pigs.org"
            },
            {
              "title": "Pig2",
              "description": "Pig!",
              "url": "www.pigs.org"
            },
            {
              "title": "Pig3",
              "description": "Pig!",
              "url": "www.pigs.org"
            },
            {
              "title": "Pig4",
              "description": "Pig!",
              "url": "www.pigs.org"
            },
            {
              "title": "Pig5",
              "description": "Pig!",
              "url": "www.pigs.org"
            },
            {
              "title": "Pig6",
              "description": "Pig!",
              "url": "www.pigs.org"
            }
          ]
        }
    """.trimIndent()

    private val stubServer = WireMockServer(port).apply {

        stubFor(
            WireMock.get(WireMock.urlEqualTo("/search?query=pigs"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withFixedDelay(Duration.ofMinutes(30).toMillis().toInt())
                )
        )
        stubFor(
            WireMock.get(WireMock.urlEqualTo("/search?query=pig"))
                .willReturn(
                    WireMock.aResponse().withBody(json)
                )
        )
    }

    fun start() = stubServer.start()
}