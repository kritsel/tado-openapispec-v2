package tadoclient.config

import org.springframework.http.*
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import java.io.IOException
import java.io.InputStream
import java.util.*


@Component
open class ChaosMonkeyUseUnknownEnumValueInterceptor : ClientHttpRequestInterceptor{

    @Throws(IOException::class)
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {

        val response = execution.execute(request, body)

        val alteredResponse: ClientHttpResponse = object : ClientHttpResponse {
            override fun getHeaders(): HttpHeaders {
                return response.headers
            }

            @Throws(IOException::class)
            override fun getBody(): InputStream {
                return ("{\"presence\": \"CHAOS\"}".byteInputStream())
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