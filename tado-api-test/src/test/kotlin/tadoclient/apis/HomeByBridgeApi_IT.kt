package tadoclient.apis

import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledIf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestClient
import tadoclient.Application
import tadoclient.TadoConfig
import tadoclient.models.BoilerMaxOutputTemperature
import tadoclient.verify.assertCorrectResponse
import tadoclient.verify.verifyObject
import kotlin.test.Test

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - home")
class HomeByBridgeApi_IT(
    // rest client to use when not testing an API method
    @Qualifier("tadoRestClient")
    val tadoRestClient: RestClient,

    // rest client to use when testing an API method,
    // this one is strict as it throws an exception when it receives an unknown JSON property
    @Qualifier("tadoStrictRestClient")
    val tadoStrictRestClient: RestClient,

    @Autowired
    tadoConfig: TadoConfig
): BaseTest(tadoConfig) {
    val tadoStrictHomeByBridgeApi = HomeByBridgeApi(tadoStrictRestClient)

    @Test
    @DisplayName("GET /homeByBridge/{bridgeId}/boilerInfo")
    @Order(10)
    @EnabledIf(value = "isBridgeConfigured", disabledReason = "no bridge specified in tado set-up")
    fun getBoilerInfo() {
        val endpoint = "GET /homeByBridge/{bridgeId}/boilerInfo"
        val boiler = assertCorrectResponse { tadoStrictHomeByBridgeApi.getBoilerInfo(tadoConfig.bridge!!.id, tadoConfig.bridge.authKey) }
        val typeName = "Boiler2"
        verifyObject(boiler, endpoint, typeName, typeName)
    }

    @Test
    @DisplayName("GET /homeByBridge/{bridgeId}/boilerMaxOutputTemperature")
    @Order(20)
    @EnabledIf(value = "isBridgeConfigured", disabledReason = "no bridge specified in tado set-up")
    fun getBoilerMaxOutputTemperature() {
        val endpoint = "GET /homeByBridge/{bridgeId}/boilerMaxOutputTemperature"
        val boilerMaxOutputTemp = assertCorrectResponse { tadoStrictHomeByBridgeApi.getBoilerMaxOutputTemperature(tadoConfig.bridge!!.id, tadoConfig.bridge.authKey) }
        val typeName = "BoilerMaxOutputTemperature"
        verifyObject(boilerMaxOutputTemp, endpoint, typeName, typeName)
    }

    @Test
    @DisplayName("PUT /homeByBridge/{bridgeId}/boilerMaxOutputTemperature")
    @Order(30)
    @Disabled("no tests executed which impact the boiler")
    fun putBoilerMaxOutputTemperature() {
        tadoStrictHomeByBridgeApi.setBoilerMaxOutputTemperature(tadoConfig.bridge!!.id, tadoConfig.bridge.authKey, BoilerMaxOutputTemperature(55f))
    }

    @Test
    @DisplayName("GET /homeByBridge/{bridgeId}/boilerWiringInstallationState")
    @Order(40)
    @EnabledIf(value = "isBridgeConfigured", disabledReason = "no bridge specified in tado set-up")
    fun getBoilerWiringInstallationState() {
        val endpoint = "GET /homeByBridge/{bridgeId}/boilerWiringInstallationState"
        val boilerWiringState = assertCorrectResponse { tadoStrictHomeByBridgeApi.getBoilerWiringInstallationState(tadoConfig.bridge!!.id, tadoConfig.bridge.authKey) }
        val typeName = "BoilerWiringInstallationState"
        verifyObject(boilerWiringState, endpoint, typeName, typeName)
    }
}