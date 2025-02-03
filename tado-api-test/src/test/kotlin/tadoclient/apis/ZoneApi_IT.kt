package tadoclient.apis

import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledIf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClient
import tadoclient.Application
import tadoclient.TadoConfig
import tadoclient.models.*
import tadoclient.verify.*
import kotlin.test.Test
import kotlin.test.assertNotEquals

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - zone")
class ZoneApi_IT(
    // rest client to use when not testing an API method
    @Qualifier("tadoRestClient")
    val tadoRestClient: RestClient,

    // rest client to use when testing an API method,
    // this one is strict as it throws an exception when it receives an unknown JSON property
    @Qualifier("tadoStrictRestClient")
    val tadoStrictRestClient: RestClient,

    @Autowired
    tadoConfig: TadoConfig
) : BaseTest(tadoConfig) {
    val tadoStrictZoneAPI = ZoneApi(tadoStrictRestClient)
    val tadoZoneAPI = ZoneApi(tadoRestClient)

    private var heatingZoneName: String? = null
    private var heatingZoneOpenWindowDetection: ZoneOpenWindowDetection? = null

    @BeforeAll
    fun before()  = try {
        heatingZoneName = tadoZoneAPI.getZones(tadoConfig.home!!.id).first { it.id == tadoConfig.zone!!.heating!!.id }.name
        heatingZoneOpenWindowDetection = tadoZoneAPI.getZones(tadoConfig.home!!.id).first { it.id == tadoConfig.zone!!.heating!!.id }.openWindowDetection
    } catch (e: Exception) {
        // ignore
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones")
    @Order(10)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getZones() {
        val endpoint = "GET /homes/{homeId}/zones"
        val zones = assertCorrectResponse { tadoStrictZoneAPI.getZones(tadoConfig.home!!.id) }
        assertNotEquals(0, zones.size)
        verifyZone(zones[0], endpoint, "response[0]")
    }

    @Test
    @DisplayName("POST /homes/{homeId}/zones")
    @Order(15)
    @Disabled("Unsuitable for weekly automated test execution: moving a device to a new zone")
    fun createZoneAndMoveDevice() {
        val endpoint = "POST /homes/{homeId}/zones"
        val input = ZoneCreate("IMPLICIT_CONTROL", ZoneType.HEATING, listOf(MoveDeviceRequest("xxx")))
        tadoStrictZoneAPI.createZone(tadoConfig.home!!.id, input, false)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/capabilities - AIR_CONDITIONING")
    @EnabledIf(value = "isHomeAndAirConZoneConfigured", disabledReason = "no home and/or AIR_CONDITIONING zone specified in tado set-up")
    @Order(20)
    fun getZoneCapabilities_AirConZone() {
       // TODO: implement for AIR_CONDITIONING
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/capabilities - HEATING")
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    @Order(21)
    fun getZoneCapabilities_HeatingZone() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/capabilities"
        val zoneCapabilities = assertCorrectResponse { tadoStrictZoneAPI.getZoneCapabilities(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id) }
        verifyZoneCapabilities(zoneCapabilities, endpoint, ancestorObjectProps = mapOf(ZONE_TYPE to ZoneType.HEATING))
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/capabilities - HOT_WATER")
    @EnabledIf(value = "isHomeAndHotWaterZoneConfigured", disabledReason = "no home and/or HOT_WATER zone specified in tado set-up")
    @Order(22)
    fun getZoneCapabilities_HotWaterZone() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/capabilities"
        val zoneCapabilities = assertCorrectResponse { tadoStrictZoneAPI.getZoneCapabilities(tadoConfig.home!!.id, tadoConfig.zone!!.hotWater!!.id) }
        verifyZoneCapabilities(zoneCapabilities, endpoint, ancestorObjectProps = mapOf(ZONE_TYPE to ZoneType.HOT_WATER))
        verifyZoneCapabilities(zoneCapabilities, endpoint, ancestorObjectProps = mapOf(ZONE_TYPE to ZoneType.HOT_WATER))
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/capabilities - 404 (unknown zoneID)")
    @Order(23)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getZoneCapabilities_404() {
        assertHttpErrorStatus(HttpStatus.NOT_FOUND)  { tadoStrictZoneAPI.getZoneCapabilities(tadoConfig.home!!.id, 99999) }
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/dazzle")
    @Order(30)
    @EnabledIf(value="isHeatingZoneConfigured", disabledReason = "no heating zone configured")
    fun putDazzle() {
        val input = DazzleInput(true)
        tadoStrictZoneAPI.setDazzle(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, input)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/dazzle")
    @Order(35)
    @EnabledIf(value="isHeatingZoneConfigured", disabledReason = "no heating zone configured")
    fun putDetails() {
        // set the zone's current name
        val input = ZoneDetailsInput(heatingZoneName)
        tadoStrictZoneAPI.setDetails(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, input)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/openWindowDetection")
    @Order(40)
    @EnabledIf(value="isHeatingZoneConfigured", disabledReason = "no heating zone configured")
    fun putOpenWindowDetection() {
        // set the zone's current open window detection settings
        val input = OpenWindowDetectionInput(
            tadoConfig.zone!!.heating!!.id,
            heatingZoneOpenWindowDetection!!.enabled,
            heatingZoneOpenWindowDetection!!.timeoutInSeconds)
        tadoStrictZoneAPI.setOpenWindowDetection(tadoConfig.home!!.id, tadoConfig.zone.heating!!.id, input)
    }

    @Test
    @DisplayName("POST /homes/{homeId}/zones/{zoneId}/state/openWindow/activate")
    @Order(41)
    @EnabledIf(value="isHeatingZoneConfigured", disabledReason = "no heating zone configured")
    fun activateOpenWindowStateForZone() {
        // no idea what this operation actually does
        tadoStrictZoneAPI.activateOpenWindowState(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)
    }

    @Test
    @DisplayName("DELETE /homes/{homeId}/zones/{zoneId}/state/openWindow")
    @Order(42)
    @EnabledIf(value="isHeatingZoneConfigured", disabledReason = "no heating zone configured")
    fun deleteOpenWindowStateForZone() {
        // no idea what this operation actually does
        tadoStrictZoneAPI.deactivateOpenWindowState(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/state - AIR_CONDITIONING")
    @EnabledIf(value = "isAirConZoneConfigured", disabledReason = "no AIR_CONDITIONING zone specified in tado set-up")
    @Order(50)
    fun getZoneState_AirCon() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/state"
        val zoneState = assertCorrectResponse { tadoStrictZoneAPI.getZoneState(tadoConfig.home!!.id, tadoConfig.zone!!.airCon!!.id) }
        verifyZoneState(zoneState, endpoint, ancestorObjectProps = mapOf(ZONE_TYPE to ZoneType.AIR_CONDITIONING))
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/state - HEATING")
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    @Order(51)
    fun getZoneState_Heating() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/state"
        val zoneState = assertCorrectResponse { tadoStrictZoneAPI.getZoneState(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id) }
        verifyZoneState(zoneState, endpoint, ancestorObjectProps = mapOf(ZONE_TYPE to ZoneType.HEATING))
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/state - HOT_WATER")
    @EnabledIf(value = "isHomeAndHotWaterZoneConfigured", disabledReason = "no home and/or HOT_WATER zone specified in tado set-up")
    @Order(52)
    fun getZoneState_HotWater() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/state"
        val zoneState = assertCorrectResponse { tadoStrictZoneAPI.getZoneState(tadoConfig.home!!.id, tadoConfig.zone!!.hotWater!!.id) }
        verifyZoneState(zoneState, endpoint, ancestorObjectProps = mapOf(ZONE_TYPE to ZoneType.HOT_WATER))
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/state - 404 (unknown zoneId)")
    @Order(53)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getZoneState_404() {
        assertHttpErrorStatus(HttpStatus.NOT_FOUND) { tadoStrictZoneAPI.getZoneState(tadoConfig.home!!.id, 99999) }
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zoneStates")
    @Order(55)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getZoneStates() {
        val endpoint = "GET /homes/{homeId}/zoneStates"
        val zoneStates = assertCorrectResponse { tadoStrictZoneAPI.getZoneStates(tadoConfig.home!!.id) }

        if (isHeatingZoneConfigured()) {
            verifyZoneState(zoneStates.zoneStates?.get(tadoConfig.zone!!.heating!!.id.toString())!!, endpoint, ancestorObjectProps = mapOf(ZONE_TYPE to ZoneType.HEATING))
        }
        if (isHotWaterZoneConfigured()) {
            verifyZoneState(zoneStates.zoneStates?.get(tadoConfig.zone!!.hotWater!!.id.toString())!!, endpoint, ancestorObjectProps = mapOf(ZONE_TYPE to ZoneType.HOT_WATER))
        }
        if (isAirConZoneConfigured()) {
            verifyZoneState(zoneStates.zoneStates?.get(tadoConfig.zone!!.airCon!!.id.toString())!!, endpoint, ancestorObjectProps = mapOf(ZONE_TYPE to ZoneType.AIR_CONDITIONING))
        }
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zoneOrder")
    @Order(60)
    @Disabled("not yet available in API definition")
    fun putZoneOrder() {
        // TODO: implement once available in API definition
    }
    
}