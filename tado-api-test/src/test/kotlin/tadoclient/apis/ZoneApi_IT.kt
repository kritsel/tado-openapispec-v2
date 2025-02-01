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
import tadoclient.models.ZoneType
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
    @Order(25)
    @Disabled("to be implemented")
    fun putDazzle() {
        // TODO: implement
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/details")
    @Order(27)
    @Disabled("to be implemented")
    fun putDetails() {
        // TODO: implement
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/openWindowDetection")
    @Order(28)
    @Disabled("to be implemented")
    fun putOpenWindowDetection() {
        // TODO: implement
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/state - AIR_CONDITIONING")
    @EnabledIf(value = "isHomeAndAirConZoneConfigured", disabledReason = "no home and/or AIR_CONDITIONING zone specified in tado set-up")
    @Order(30)
    fun getZoneState_AirCon() {
        // not possible at the moment
        // TODO: implement for AIR_CONDITIONING
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/state - HEATING")
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    @Order(31)
    fun getZoneState_Heating() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/state"
        val zoneState = assertCorrectResponse { tadoStrictZoneAPI.getZoneState(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id) }
        verifyZoneState(zoneState, endpoint, ancestorObjectProps = mapOf(ZONE_TYPE to ZoneType.HEATING))
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/state - HOT_WATER")
    @EnabledIf(value = "isHomeAndHotWaterZoneConfigured", disabledReason = "no home and/or HOT_WATER zone specified in tado set-up")
    @Order(32)
    fun getZoneState_HotWater() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/state"
        val zoneState = assertCorrectResponse { tadoStrictZoneAPI.getZoneState(tadoConfig.home!!.id, tadoConfig.zone!!.hotWater!!.id) }
        verifyZoneState(zoneState, endpoint, ancestorObjectProps = mapOf(ZONE_TYPE to ZoneType.HOT_WATER))
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/state - 404 (unknown zoneId)")
    @Order(33)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getZoneState_404() {
        assertHttpErrorStatus(HttpStatus.NOT_FOUND) { tadoStrictZoneAPI.getZoneState(tadoConfig.home!!.id, 99999) }
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zoneOrder")
    @Order(35)
    @Disabled("not yet available in spec")
    fun putZoneOrder() {
        // TODO: implement once available in spec
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zoneStates")
    @Order(40)
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
    
}