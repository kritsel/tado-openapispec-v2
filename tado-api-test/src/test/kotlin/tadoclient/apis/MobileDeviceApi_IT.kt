package tadoclient.apis

import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledIf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestClient
import tadoclient.Application
import tadoclient.TadoConfig
import tadoclient.models.MobileDeviceSettings
import tadoclient.verify.assertCorrectResponse
import tadoclient.verify.verifyMobileDevice
import tadoclient.verify.verifyObject
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

/**
 * Not tested: DELETE /homes/{homeId}/mobileDevices/{mobileDeviceId} as it would be a destructive test
 */

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - mobile device")
class MobileDeviceApi_IT(
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
    val tadoStrictMobileDeviceAPI = MobileDeviceApi(tadoStrictRestClient)
    val tadoMobileDeviceAPI = MobileDeviceApi(tadoRestClient)

    @Test
    @DisplayName("GET /homes/{homeId}/mobileDevices")
    @Order(10)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getMobileDevices() {
        val endpoint = "GET /homes/{homeId}/mobileDevices"
        val mobileDevices = assertCorrectResponse { tadoStrictMobileDeviceAPI.getMobileDevices(tadoConfig.home!!.id) }
        assertNotNull(mobileDevices)
        assertNotEquals(0, mobileDevices.size)
        verifyMobileDevice(mobileDevices[0], endpoint, "response[0]")
    }

    @Test
    @DisplayName("GET /homes/{homeId}/mobileDevices/{mobileDeviceId}")
    @Order(20)
    @EnabledIf(value = "isHomeAndMobileDeviceConfigured", disabledReason = "no home and/or mobile device specified in tado set-up")
    fun getMobileDevice() {
        val endpoint = "GET /homes/{homeId}/mobileDevices/{mobileDeviceId}"
        val mobileDevice = assertCorrectResponse { tadoStrictMobileDeviceAPI.getMobileDevice(tadoConfig.home!!.id, tadoConfig.mobileDevice!!.id) }
        assertNotNull(mobileDevice)
        verifyMobileDevice(mobileDevice, endpoint)
    }

    @Test
    @DisplayName("DELETE /homes/{homeId}/mobileDevices/{mobileDeviceId}")
    @Order(30)
    @Disabled("Skipped because of destructive nature of the testcase (and the fact that it is non-trivial to recreate the initial situation)")
    fun deleteMobileDevice() {
        // no implementation
    }

    @Test
    @DisplayName("GET /homes/{homeId}/mobileDevices/{mobileDeviceId}/settings")
    @Order(40)
    @EnabledIf(value = "isHomeAndMobileDeviceConfigured", disabledReason = "no home and/or mobile device specified in tado set-up")
    fun getMobileDeviceSettings() {
        val endpoint = "GET /homes/{homeId}/mobileDevices/{mobileDeviceId}/settings"
        val settings = assertCorrectResponse { tadoStrictMobileDeviceAPI.getMobileDeviceSettings(tadoConfig.home!!.id, tadoConfig.mobileDevice!!.id) }
        val typeName= "MobileDeviceSettings"
        verifyObject(settings, endpoint, typeName, typeName)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/mobileDevices/{mobileDeviceId}/settings")
    @Order(50)
    @EnabledIf(value = "isHomeAndMobileDeviceConfigured", disabledReason = "no home and/or mobile device specified in tado set-up")
    fun putMobileDeviceSettings() {
        // first get the current mobile device settings
        val settings = tadoMobileDeviceAPI.getMobileDeviceSettings(tadoConfig.home!!.id, tadoConfig.mobileDevice!!.id)
        // then test by only setting the 'geoTrackingEnabled' setting again
        val input = MobileDeviceSettings(geoTrackingEnabled = settings.geoTrackingEnabled)
        tadoStrictMobileDeviceAPI.setMobileDeviceSettings(tadoConfig.home.id, tadoConfig.mobileDevice.id, settings)
    }
}