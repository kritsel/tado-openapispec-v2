package tadoclient.apis

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClient
import tadoclient.Application
import tadoclient.verify.assertNoHttpErrorStatus
import tadoclient.verify.verifyMobileDevice
import tadoclient.verify.verifyNested
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
    @Qualifier("tadoRestClient")
    val tadoRestClient: RestClient
) {
    val tadoMobileDeviceAPI = MobileDeviceApi(tadoRestClient)

    @Test
    @DisplayName("GET /homes/{homeId}/mobileDevices")
    @Order(10)
    fun getMobileDevices() {
        val endpoint = "GET /homes/{homeId}/mobileDevices"
        val mobileDevices = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoMobileDeviceAPI.getMobileDevices(HOME_ID)
        }
        assertNotNull(mobileDevices)
        assertNotEquals(0, mobileDevices.size)
        verifyMobileDevice(mobileDevices[0], endpoint, "response[0]")
    }

    @Test
    @DisplayName("GET /homes/{homeId}/mobileDevices/{mobileDeviceId}")
    @Order(20)
    fun getMobileDevice() {
        val endpoint = "GET /homes/{homeId}/mobileDevices/{mobileDeviceId}"
        val mobileDevice = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoMobileDeviceAPI.getMobileDevice(HOME_ID, MOBILE_DEVICE_ID)
        }
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
    fun getMobileDeviceSettings() {
        val endpoint = "GET /homes/{homeId}/mobileDevices/{mobileDeviceId}/settings"
        val settings = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoMobileDeviceAPI.getMobileDeviceSettings(HOME_ID, MOBILE_DEVICE_ID)
        }
        val typeName= "MobileDeviceSettings"
        verifyNested(settings, endpoint, typeName, typeName)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/mobileDevices/{mobileDeviceId}/settings")
    @Order(50)
    @Disabled("not yet implemented")
    fun putMobileDeviceSettings() {
        // TDDO: implement put mobile settings
    }
}