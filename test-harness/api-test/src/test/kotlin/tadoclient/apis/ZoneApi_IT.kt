package tadoclient.apis

import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledIf
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClient
import tadoclient.Application
import tadoclient.models.ZoneType
import tadoclient.verify.*
import kotlin.test.Test
import kotlin.test.assertNotEquals

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - zone")
class ZoneApi_IT(
    @Qualifier("tadoRestClient")
    val tadoRestClient: RestClient,

    @Value("\${tado.home.id:-1}")
    val homeId: Long,

    @Value("\${tado.zone.heating.id:-1}")
    val heatingZoneId: Int,

    @Value("\${tado.zone.hot-water.id:-1}")
    val hotWaterZoneId: Int,

    @Value("\${tado.zone.air-con.id:-1}")
    val airConZoneId: Int,

    @Value("\${tado.zone.hot-water.can-set-temperature:false}")
    val hotWaterZoneCanSetTemperature: Boolean
) {
    val tadoZoneAPI = ZoneApi(tadoRestClient)

    @Test
    @DisplayName("GET /homes/{homeId}/zones")
    @Order(10)
    fun getZones() {
        val endpoint = "GET /homes/{homeId}/zones"
        val zones = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoZoneAPI.getZones(HOME_ID)
        }
        assertNotEquals(0, zones.size)
        verifyZone(zones[0], endpoint, "response[0]")
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/capabilities - AIR_CONDITIONING")
    @EnabledIf(value = "isAirConZoneAvailable", disabledReason = "no AIR_CONDITIONING zone available in tado set-up")
    @Order(20)
    fun getZoneCapabilities_AirConZone() {
       // TODO: implement for AIR_CONDITIONING
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/capabilities - HEATING")
    @EnabledIf(value = "isHeatingZoneAvailable", disabledReason = "no HEATING zone available in tado set-up")
    @Order(21)
    fun getZoneCapabilities_HeatingZone() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/capabilities"
        val zoneCapabilities = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoZoneAPI.getZoneCapabilities(homeId, heatingZoneId)
        }
        verifyZoneCapabilities(Pair(ZoneType.HEATING, true), zoneCapabilities, endpoint)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/capabilities - HOT_WATER")
    @EnabledIf(value = "isHotWaterZoneAvailable", disabledReason = "no HOT_WATER zone available in tado set-up")
    @Order(22)
    fun getZoneCapabilities_HotWaterZone() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/capabilities"
        val zoneCapabilities = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoZoneAPI.getZoneCapabilities(homeId, hotWaterZoneId)
        }
        verifyZoneCapabilities(Pair(ZoneType.HOT_WATER, hotWaterZoneCanSetTemperature), zoneCapabilities, endpoint)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/capabilities - 404 (unknown zoneID)")
    @Order(23)
    fun getZoneCapabilities_404() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/capabilities"
        assertHttpErrorStatus(HttpStatus.NOT_FOUND) {
            tadoZoneAPI.getZoneCapabilities(homeId, 99999)
        }
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/dazzle")
    @Order(25)
    @Disabled("not yet available n spec")
    fun putDazzle() {
        // TODO: implement once in spec
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/details")
    @Order(27)
    @Disabled("not yet available n spec")
    fun putDetails() {
        // TODO: implement once in spec
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/openWindowDetection")
    @Order(28)
    @Disabled("not yet available n spec")
    fun putOpenWindowDetection() {
        // TODO: implement once in spec
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/state - AIR_CONDITIONING")
    @EnabledIf(value = "isAirConZoneAvailable", disabledReason = "no AIR_CONDITIONING zone available in tado set-up")
    @Order(30)
    fun getZoneState_AirCon() {
        // not possible at the moment
        // TODO: implement for AIR_CONDITIONING
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/state - HEATING")
    @EnabledIf(value = "isHeatingZoneAvailable", disabledReason = "no HEATING zone available in tado set-up")
    @Order(31)
    fun getZoneState_Heating() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/state"
        val zoneState = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoZoneAPI.getZoneState(homeId, heatingZoneId)
        }
        verifyZoneState(Pair(ZoneType.HEATING, true), zoneState, endpoint)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/state - HOT_WATER")
    @EnabledIf(value = "isHotWaterZoneAvailable", disabledReason = "no HOT_WATER zone available in tado set-up")
    @Order(32)
    fun getZoneState_HotWater() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/state"
        val zoneState = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoZoneAPI.getZoneState(homeId, hotWaterZoneId)
        }
        verifyZoneState(Pair(ZoneType.HOT_WATER, hotWaterZoneCanSetTemperature), zoneState, endpoint)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/state - 404 (unknown zoneId)")
    @Order(33)
    fun getZoneState_404() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/state"
        assertHttpErrorStatus(HttpStatus.NOT_FOUND) {
            tadoZoneAPI.getZoneState(homeId, 99999)
        }
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
    fun getZoneStates() {
        val endpoint = "GET /homes/{homeId}/zoneStates"
        val zoneStates = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoZoneAPI.getZoneStates(homeId)
        }

        if (isHeatingZoneAvailable()) {
            verifyZoneState(Pair(ZoneType.HEATING, true), zoneStates.zoneStates?.get("$heatingZoneId")!!, endpoint)
        }
        if (isHotWaterZoneAvailable()) {
            verifyZoneState(Pair(ZoneType.HOT_WATER, hotWaterZoneCanSetTemperature), zoneStates.zoneStates?.get("$hotWaterZoneId")!!, endpoint)
        }
        if (isAirConZoneAvailable()) {
            verifyZoneState(Pair(ZoneType.AIR_CONDITIONING, true), zoneStates.zoneStates?.get("$airConZoneId")!!, endpoint)
        }
    }

    fun isAirConZoneAvailable() : Boolean {
        return airConZoneId >= 0
    }

    fun isHeatingZoneAvailable() : Boolean {
        return heatingZoneId >= 0
    }

    fun isHotWaterZoneAvailable() : Boolean {
        return hotWaterZoneId >= 0
    }

}