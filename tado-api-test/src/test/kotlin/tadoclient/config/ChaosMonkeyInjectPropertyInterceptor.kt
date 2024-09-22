package tadoclient.config

import org.springframework.http.*
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.math.min


@Component
open class ChaosMonkeyInjectPropertyInterceptor : ClientHttpRequestInterceptor{

    @Throws(IOException::class)
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {

        val response = execution.execute(request, body)
        val responseBody = String(response.body.readAllBytes(), StandardCharsets.UTF_8)
        val alteredResponseBody = responseBody.replaceFirst("{", "{\"chaos\":\"monkey\",")

        System.out.println("original response: ${responseBody.substring(0, min(responseBody.length, 50))}")
        System.out.println("altered response: ${alteredResponseBody.substring(0, min(alteredResponseBody.length, 50))}")

        val alteredResponse: ClientHttpResponse = object : ClientHttpResponse {
            override fun getHeaders(): HttpHeaders {
                return response.headers
            }

            @Throws(IOException::class)
            override fun getBody(): InputStream {
                return (alteredResponseBody.byteInputStream())
            }

            @Throws(IOException::class)
            override fun getStatusCode(): HttpStatusCode {
                return response.statusCode
            }

            @Throws(IOException::class)
            override fun getStatusText(): String {
                return response.statusText
            }

            override fun close() {
            }
        }
        return alteredResponse
    }
}