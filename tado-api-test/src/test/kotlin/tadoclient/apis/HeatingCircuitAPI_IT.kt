package tadoclient.apis

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.junit.jupiter.api.condition.EnabledIf
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestClient
import tadoclient.Application
import tadoclient.TadoConfig
import tadoclient.verify.assertCorrectResponse
import tadoclient.verify.verifyHeatingCircuit
import kotlin.test.assertNotEquals

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - heating circuit")
class HeatingCircuitAPI_IT(
    @Qualifier("tadoStrictRestClient")
    val tadoStrictRestClient: RestClient,

    @Autowired
    tadoConfig: TadoConfig
): BaseTest(tadoConfig) {
    val tadoStrictHeatingCircuitApiAPI = HeatingCircuitApi(tadoStrictRestClient)

    @Test
    @DisplayName("GET /homes/{homeId}/heatingCircuits")
    @Order(10)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getHeatingCircuits() {
        val endpoint = "GET /homes/{homeId}/heatingCircuits"
        val heatingCircuits = assertCorrectResponse { tadoStrictHeatingCircuitApiAPI.getHeatingCircuits(tadoConfig.home!!.id) }
        assertNotEquals(0, heatingCircuits.size)
        heatingCircuits.forEachIndexed {i, elem -> verifyHeatingCircuit(elem, "$endpoint[$i]") }
    }

}