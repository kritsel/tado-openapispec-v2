package tadoclient.apis

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.junit.jupiter.api.condition.EnabledIf
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestClient
import tadoclient.Application
import tadoclient.TadoConfig
import tadoclient.verify.*
import kotlin.test.assertNotEquals

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - heating circuit")
class BridgeApi_IT(
    @Qualifier("tadoStrictRestClient")
    val tadoStrictRestClient: RestClient,

    @Autowired
    tadoConfig: TadoConfig
): BaseTest(tadoConfig) {
    val tadoStrictBridgeAPI = BridgeApi(tadoStrictRestClient)

    @Test
    @DisplayName("GET /bridges/{bridgeId}")
    @Order(10)
    @EnabledIf(value = "isBridgeConfigured", disabledReason = "no bridge specified in tado set-up")
    fun getHeatingCircuits() {
        val endpoint = "GET /bridges/{bridgeId}"
        val bridge = assertCorrectResponse { tadoStrictBridgeAPI.getBridge(tadoConfig.bridge!!.id, tadoConfig.bridge.authKey) }
        verifyBridge(bridge, endpoint)
    }
}