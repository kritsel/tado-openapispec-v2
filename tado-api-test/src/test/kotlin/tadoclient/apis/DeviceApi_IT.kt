package tadoclient.apis

import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledIf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestClient
import tadoclient.Application
import tadoclient.TadoConfig
import tadoclient.models.ChildLock
import tadoclient.models.MoveDeviceRequest
import tadoclient.models.Temperature
import tadoclient.verify.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - device")
class DeviceApi_IT (
    @Qualifier("tadoStrictRestClient")
    val tadoStrictRestClient: RestClient,

    @Qualifier("tadoRestClient")
    val tadoRestClient: RestClient,

    @Autowired
    tadoConfig: TadoConfig
): BaseTest(tadoConfig) {
    val tadoStrictDeviceAPI = DeviceApi(tadoStrictRestClient)
    val tadoDeviceAPI = DeviceApi(tadoRestClient)

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
    @DisplayName("PUT /devices/{deviceId}/childLock")
    @Order(5)
    @EnabledIf(value = "isThermostatDeviceConfigured", disabledReason = "no thermostat device specified in tado set-up")
    fun setChildLock() {
        // first get the current child lock setting
        val device = tadoDeviceAPI.getDevice(tadoConfig.device!!.thermostat!!.id)
        // then test by re-setting the same childLock setting
        tadoStrictDeviceAPI.setChildLock(tadoConfig.device!!.thermostat!!.id, ChildLock(device.childLockEnabled))
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
        verifyObject(offset, endpoint, typeName, typeName)
    }

    @Test
    @DisplayName("PUT /devices/{deviceId}/temperatureOffset")
    @Order(30)
    @EnabledIf(value = "isThermostatDeviceConfigured", disabledReason = "no thermostat device specified in tado set-up")
    fun putTemperatureOffset() {
        val endpoint = "PUT /devices/{deviceId}/temperatureOffset"
        val offset = assertCorrectResponse { tadoStrictDeviceAPI.setTemperatureOffset(tadoConfig.device!!.thermostat!!.id, Temperature(0.5f)) }
        val typeName = "TemperatureOffset"
        verifyObject(offset, endpoint, typeName, typeName)
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
        // observation: returned an empty array until an aircon controller was added to the home
        installations.forEachIndexed { i, elem -> verifyInstallation(elem, endpoint, "response[$i]") }
    }

    @Test
    @DisplayName("GET /homes/{homeId}/installations/{installationId}")
    @Order(66)
    @EnabledIf(value = "isInstallationConfigured", disabledReason = "no home specified in tado set-up")
    fun getInstallation() {
        val endpoint = "GET /homes/{homeId}/installations/{installationId}"
        val installation = assertCorrectResponse { tadoStrictDeviceAPI.getInstallation(tadoConfig.home!!.id, tadoConfig.installation!!.id) }
        verifyInstallation(installation, endpoint)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/control")
    @Order(70)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getControl() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/control"
        val zoneControl = assertCorrectResponse { tadoStrictDeviceAPI.getZoneControl(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id) }

        val typeName = "ZoneControl"
        verifyObject(zoneControl, endpoint, endpoint, typeName,
            nullAllowedProperties = listOf(
                "$typeName.duties.driver",
                "$typeName.duties.drivers",
                "$typeName.duties.leader",
                "$typeName.duties.leaders",
                "$typeName.duties.ui",
                "$typeName.duties.uis")
        )
    }

    // integration test for "GET /homes/{homeId}/zones/{zoneId}/control"
    // see HeatingCircuitAPI_IT

    @Test
    @DisplayName("POST /homes/{homeId}/zones/{zoneId}/devices")
    @Order(80)
    @Disabled("Unsuitable for weekly automated test execution: moving a device to another zone")
    fun moveDevice() {
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
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home specified in tado set-up")
    fun putMeasuringDevice() {
        // first get the current measuring device and simply put the same one again
        val measuringDevice = tadoDeviceAPI.getZoneMeasuringDevice(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)
        // now test
        val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/measuringDevice"
        val device = assertCorrectResponse { tadoStrictDeviceAPI.setZoneMeasuringDevice(tadoConfig.home.id, tadoConfig.zone.heating!!.id, MoveDeviceRequest(measuringDevice.serialNo)) }
        verifyDevice(device, endpoint)
    }

}