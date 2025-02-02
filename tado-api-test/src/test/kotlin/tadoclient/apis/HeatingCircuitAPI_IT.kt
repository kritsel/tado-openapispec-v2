package tadoclient.apis

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.junit.jupiter.api.condition.EnabledIf
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestClient
import tadoclient.Application
import tadoclient.TadoConfig
import tadoclient.models.HeatingCircuitInput
import tadoclient.verify.assertCorrectResponse
import tadoclient.verify.verifyHeatingCircuit
import tadoclient.verify.verifyZoneControl
import kotlin.test.assertNotEquals

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - heating circuit")
class HeatingCircuitAPI_IT(
    @Qualifier("tadoStrictRestClient")
    val tadoStrictRestClient: RestClient,

    @Qualifier("tadoRestClient")
    val tadoRestClient: RestClient,

    @Autowired
    tadoConfig: TadoConfig
): BaseTest(tadoConfig) {
    val tadoStrictHeatingCircuitApiAPI = HeatingCircuitApi(tadoStrictRestClient)
    val tadoDeviceAPI = DeviceApi(tadoRestClient)

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

    // Integration test for GET /homes/{homeId}/zones/{zoneId}/control
    // see DeviceApi_IT
    // (the operation belongs to multiple API operation groups, and the 'device' group is considered to be the primary one)

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/control/heatingCircuit")
    @Order(20)
    @EnabledIf(value = "isHeatingZoneConfigured", disabledReason = "no home specified in tado set-up")
    fun setHeatingCircuitForZone() {
        // first get the zones current heating circuit
        val currentZoneControl = tadoDeviceAPI.getZoneControl(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)
        // only continue when this zone has a heating circuit
        currentZoneControl.heatingCircuit?.let {
            val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/control/heatingCircuit"
            val input = HeatingCircuitInput(currentZoneControl.heatingCircuit)
            val zoneControl = assertCorrectResponse {
                tadoStrictHeatingCircuitApiAPI.setHeatingCircuit(
                    tadoConfig.home.id,
                    tadoConfig.zone.heating!!.id,
                    input
                )
            }
            verifyZoneControl(zoneControl, endpoint)
        }
    }
}