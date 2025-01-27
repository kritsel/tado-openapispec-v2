package tadoclient.apis

import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledIf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestClient
import tadoclient.Application
import tadoclient.TadoConfig
import tadoclient.models.Temperature
import tadoclient.verify.assertCorrectResponse
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
    @Qualifier("tadoStrictRestClient")
    val tadoStrictRestClient: RestClient,

    @Autowired
    tadoConfig: TadoConfig
): BaseTest(tadoConfig) {
    val tadoStrictDeviceAPI = DeviceApi(tadoStrictRestClient)

    @Test
    @DisplayName("GET /devices/{deviceId}")
    @Order(5)
    @EnabledIf(value = "isThermostatDeviceConfigured", disabledReason = "no thermostat device specified in tado set-up")
    fun getDevice() {
        val endpoint = "GET /devices/{deviceId}"
        val device = assertCorrectResponse { tadoStrictDeviceAPI.getDevice(tadoConfig.device!!.thermostat!!.id) }
        verifyDevice(device, endpoint)
    }

    @Test
    @DisplayName("POST /devices/{deviceId}/identify")
    @Order(10)
    @EnabledIf(value = "isThermostatDeviceConfigured", disabledReason = "no thermostat device specified in tado set-up")
    fun identifyDevice() {
        assertCorrectResponse { tadoStrictDeviceAPI.identifyDevice(tadoConfig.device!!.thermostat!!.id) }
    }

    @Test
    @DisplayName("GET /devices/{deviceId}/temperatureOffset")
    @Order(20)
    @EnabledIf(value = "isThermostatDeviceConfigured", disabledReason = "no thermostat device specified in tado set-up")
    fun getTemperatureOffset() {
        val endpoint = "GET /devices/{deviceId}/temperatureOffset"
        val offset = assertCorrectResponse { tadoStrictDeviceAPI.getTemperatureOffset(tadoConfig.device!!.thermostat!!.id) }
        val typeName = "TemperatureOffset"
        verifyNested(offset, endpoint, typeName, typeName)
    }

    @Test
    @DisplayName("PUT /devices/{deviceId}/temperatureOffset")
    @Order(30)
    @EnabledIf(value = "isThermostatDeviceConfigured", disabledReason = "no thermostat device specified in tado set-up")
    fun putTemperatureOffset() {
        val endpoint = "OYT /devices/{deviceId}/temperatureOffset"
        val offset = assertCorrectResponse { tadoStrictDeviceAPI.setTemperatureOffset(tadoConfig.device!!.thermostat!!.id, Temperature(0.5f)) }
        val typeName = "TemperatureOffset"
        verifyNested(offset, endpoint, typeName, typeName)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/devices")
    @Order(40)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getDevices() {
        val endpoint = "GET /homes/{homeId}/devices"
        val devices = assertCorrectResponse { tadoStrictDeviceAPI.getDevices(tadoConfig.home!!.id) }

        // check devices
        assertNotEquals(0, devices.size)
        devices.forEachIndexed { i, elem -> verifyDevice(elem, endpoint, "response[$i]") }
    }

    @Test
    @DisplayName("GET /homes/{homeId}/deviceList")
    @Order(60)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getDeviceList() {
        val endpoint = "GET /homes/{homeId}/deviceList"
        val deviceList = assertCorrectResponse { tadoStrictDeviceAPI.getDeviceList(tadoConfig.home!!.id) }

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
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getInstallations() {
        val endpoint = "GET /homes/{homeId}/installations"
        val installations = assertCorrectResponse { tadoStrictDeviceAPI.getInstallations(tadoConfig.home!!.id) }

        // returns AC installations
        assertEquals(1, installations.size)

        // todo: implement an verifyInstallation method
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/control")
    @Order(70)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getControl() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/control"
        val zoneControl = assertCorrectResponse { tadoStrictDeviceAPI.getZoneControl(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id) }

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
    @Disabled("to be implemented")
    fun putControl() {
        // TODO: to be implemented
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/measuringDevice")
    @Order(90)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home specified in tado set-up")
    fun getMeasuringDevice() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/measuringDevice"
        val device = assertCorrectResponse { tadoStrictDeviceAPI.getZoneMeasuringDevice(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id) }
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