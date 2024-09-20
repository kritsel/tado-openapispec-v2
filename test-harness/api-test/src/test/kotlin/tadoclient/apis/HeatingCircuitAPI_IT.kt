package tadoclient.apis

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClient
import tadoclient.Application
import tadoclient.models.HeatingSystem
import tadoclient.verify.assertNoHttpErrorStatus
import tadoclient.verify.verifyHeatingCircuit
import kotlin.test.assertNotEquals

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - heating circuit")
class HeatingCircuitAPI_IT(
    @Qualifier("tadoRestClient")
    val tadoRestClient: RestClient
) {
    val tadoHeatingCircuitApiAPI = HeatingCircuitApi(tadoRestClient)

    var heatingSystemBeforeTest: HeatingSystem? = null

    @Test
    @DisplayName("GET /homes/{homeId}/heatingCircuits")
    @Order(10)
    fun getHeatingCircuits() {
        val endpoint = "GET /homes/{homeId}/heatingCircuits"
        val heatingCircuits = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoHeatingCircuitApiAPI.getHeatingCircuits(HOME_ID)
        }
        assertNotEquals(0, heatingCircuits.size)
        heatingCircuits.forEachIndexed {i, elem -> verifyHeatingCircuit(elem, "$endpoint[$i]") }
    }

}