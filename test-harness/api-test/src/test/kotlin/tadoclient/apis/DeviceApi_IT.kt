package tadoclient.apis

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestClient
import tadoclient.Application
import tadoclient.TadoConfig
import tadoclient.models.Temperature
import tadoclient.verify.assertNoHttpErrorStatus
import tadoclient.verify.verifyDevice
import tadoclient.verify.verifyDeviceListItem
import tadoclient.verify.verifyNested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - device")
class DeviceApi_IT (
    @Qualifier("tadoRestClient")
    val tadoRestClient: RestClient
) {
    val tadoDeviceAPI = DeviceApi(tadoRestClient)

    @Autowired
    lateinit var tadoConfig: TadoConfig

    @Test
    @DisplayName("POST /devices/{deviceId}")
    @Order(5)
    fun getDevice() {
        println("username from tadoConfig: ${tadoConfig.username}")
        val endpoint = "GET /devices/{deviceId}"
        val device = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoDeviceAPI.getDevice(VA_DEVICE)
        }
        verifyDevice(device, endpoint)
    }

    @Test
    @DisplayName("POST /devices/{deviceId}/identify")
    @Order(10)
    fun identifyDevice() {
        val endpoint = "POST /devices/{deviceId}/identify"
        val result = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoDeviceAPI.identifyDevice(VA_DEVICE)
        }

        assertEquals(Unit, result)
    }

    @Test
    @DisplayName("GET /devices/{deviceId}/temperatureOffset")
    @Order(20)
    fun getTemperatureOffset() {
        val endpoint = "GET /devices/{deviceId}/temperatureOffset"
        val offset = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoDeviceAPI.getTemperatureOffset(VA_DEVICE)
        }
        val typeName = "TemperatureOffset"
        verifyNested(offset, endpoint, typeName, typeName)
    }

    @Test
    @DisplayName("PUT /devices/{deviceId}/temperatureOffset")
    @Order(30)
    fun putTemperatureOffset() {
        val endpoint = "OYT /devices/{deviceId}/temperatureOffset"
        val offset = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoDeviceAPI.setTemperatureOffset(VA_DEVICE, Temperature(0.5f))
        }
        val typeName = "TemperatureOffset"
        verifyNested(offset, endpoint, typeName, typeName)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/devices")
    @Order(40)
    fun getDevices() {
        val endpoint = "GET /homes/{homeId}/devices"
        val devices = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoDeviceAPI.getDevices(HOME_ID)
        }

        // check devices
        assertNotEquals(0, devices.size)
        devices.forEachIndexed { i, elem -> verifyDevice(elem, endpoint, "response[$i]") }
    }

    @Test
    @DisplayName("GET /homes/{homeId}/deviceList")
    @Order(60)
    fun getDeviceList() {
        val endpoint = "GET /homes/{homeId}/deviceList"
        val deviceList = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoDeviceAPI.getDeviceList(HOME_ID)
        }

        assertNotEquals(0, deviceList.propertyEntries?.size)
        deviceList.propertyEntries?.forEachIndexed { i, elem ->
            verifyDeviceListItem(
                elem,
                endpoint,
                "response.entries[$i]"
            )
        }
    }

    @Test
    @DisplayName("GET /homes/{homeId}/installations")
    @Order(65)
    fun getInstallations() {
        val endpoint = "GET /homes/{homeId}/installations"
        val installations = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoDeviceAPI.getInstallations(HOME_ID)
        }

        // this operation appears to be practically deprected, as it always returns an empty array
        // let's verify if that is still the case
        assertEquals(0, installations.size)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/control")
    @Order(70)
    fun getControl() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/control"
        val zoneControl = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoDeviceAPI.getZoneControl(HOME_ID, ZONE_ID)
        }

        val typeName = "ZoneControl"
        verifyNested(zoneControl, endpoint, endpoint, typeName,
            nullAllowedProperties = listOf(
                "$typeName.duties.driver",
//                "$typeName.duties.drivers",
                "$typeName.duties.leader",
                "$typeName.duties.leaders",
                "$typeName.duties.ui",
                "$typeName.duties.uis"),
            stopAtProperties = listOf(
                "$typeName.duties.driver",
                "$typeName.duties.drivers",
                "$typeName.duties.leader",
                "$typeName.duties.leaders",
                "$typeName.duties.ui",
                "$typeName.duties.uis"))

        // verify duties.driver
        zoneControl.duties?.driver?.let {
            verifyDevice(it, endpoint, "$endpoint.duties.driver")
        }

        // verify duties.leader
        zoneControl.duties?.leader?.let {
            verifyDevice(it, endpoint, "$endpoint.duties.leader")
        }

        // verify duties.ui
        zoneControl.duties?.ui?.let {
            verifyDevice(it, endpoint, "$endpoint.duties.ui")
        }

        // verify duties.drivers
        zoneControl.duties?.drivers?.let {
            it.forEachIndexed { i, device -> verifyDevice(device, endpoint, "$endpoint.duties.drivers[$i]")}
        }

        // verify duties.leaders
        zoneControl.duties?.leaders?.let {
            it.forEachIndexed { i, device -> verifyDevice(device, endpoint, "$endpoint.duties.leaders[$i]")}
        }

        // verify duties.leaders
        zoneControl.duties?.uis?.let {
            it.forEachIndexed { i, device -> verifyDevice(device, endpoint, "$endpoint.uis.uis[$i]") }
        }
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/control/heatingCircuit")
    @Order(80)
    @Disabled("not yet available in the OpenAPI spec")
    fun putControl() {
        // TODO: once available in the spec
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/measuringDevice")
    @Order(90)
    fun getMeasuringDevice() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/measuringDevice"
        val device = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoDeviceAPI.getZoneMeasuringDevice(HOME_ID, ZONE_ID)
        }
        verifyDevice(device, endpoint)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/measuringDevice")
    @Order(100)
    @Disabled("not yet implemented")
    fun putMeasuringDevice() {
        //TODO: to be implemented
    }

}