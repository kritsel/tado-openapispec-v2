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
import tadoclient.verify.assertCorrectResponse
import tadoclient.verify.assertHttpErrorStatus
import tadoclient.verify.verifyHomeState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - home control")
class HomeControlApi_IT(
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
    val tadoHomeControlAPI = HomeControlApi(tadoRestClient)
    val tadoStrictHomeControlAPI = HomeControlApi(tadoStrictRestClient)
    val tadoZoneControlAPI = ZoneControlApi(tadoRestClient)

    var zoneOverlaysBeforeTest: MutableMap<Int, ZoneOverlay?> = mutableMapOf()
    var homeStateBeforeTest: HomeState? = null

    // capture the current state of anything that will be changed in this test class before we start running the tests
    @BeforeAll
    fun before()  {
        try {
            if (isHomeAndHeatingZoneConfigured()) {
                zoneOverlaysBeforeTest[tadoConfig.zone!!.heating!!.id] =
                    tadoZoneControlAPI.getZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone.heating!!.id)
            }
            if (isHomeConfigured()) {
                homeStateBeforeTest = tadoHomeControlAPI.getHomeState(tadoConfig.home!!.id)
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    @AfterAll()
    fun after() {
        // ZoneOverlay
        if (isHomeAndHeatingZoneConfigured()) {
            // 1. delete
            try {
                tadoZoneControlAPI.deleteZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)
            } catch (e: Exception) {
                // ignore
            }
            // 2. recreate
            if (zoneOverlaysBeforeTest[tadoConfig.zone!!.heating!!.id] != null) {
                val beforeOverlay = zoneOverlaysBeforeTest[tadoConfig.zone.heating!!.id]
                val newZoneOverlay = ZoneOverlay(
                    setting = ZoneSetting(
                        type = beforeOverlay?.setting?.type,
                        power = beforeOverlay?.setting?.power,
                        temperature = if (beforeOverlay?.setting?.power == Power.ON)
                            Temperature(celsius = beforeOverlay?.setting?.temperature?.celsius) else null
                    ),
                    termination = ZoneOverlayTermination(
                        typeSkillBasedApp = beforeOverlay?.termination?.typeSkillBasedApp,
                        durationInSeconds = if (beforeOverlay?.termination?.typeSkillBasedApp == ZoneOverlayTerminationTypeSkillBasedApp.TIMER)
                            beforeOverlay.termination?.remainingTimeInSeconds else null
                    )
                )
                tadoZoneControlAPI.setZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, newZoneOverlay)
            }
        }

        // presence in home
        if (isHomeConfigured()) {
            homeStateBeforeTest?.let {
                if (it.presenceLocked == true) {
                    tadoHomeControlAPI.setPresenceLock(tadoConfig.home!!.id, PresenceLock(it.presence))
                }
            }
        }

    }

    @Test
    @DisplayName("POST /homes/{homeId}/overlay")
    @Order(10)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun setZoneOverlays() {
        val newZoneOverlay = ZoneOverlay(
            setting = ZoneSetting(
                type = ZoneType.HEATING,
                power = Power.ON,
                temperature = Temperature(celsius = 16f)
            ),
            termination = ZoneOverlayTermination(
                typeSkillBasedApp = ZoneOverlayTerminationTypeSkillBasedApp.TIMER,
                durationInSeconds = 120
            )
        )
        val result = assertCorrectResponse {
            tadoStrictHomeControlAPI.setZoneOverlays(tadoConfig.home!!.id, ZoneOverlays(listOf(ZoneOverlaysOverlaysInner(tadoConfig.zone!!.heating!!.id.toString(), newZoneOverlay))) )
        }
        assertEquals(Unit, result)
    }

    @Test
    @DisplayName("DELETE /homes/{homeId}/overlay")
    @Order(20)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun deleteZoneOverlays() {
        val result = assertCorrectResponse { tadoStrictHomeControlAPI.deleteZoneOverlays(tadoConfig.home!!.id, rooms = listOf(tadoConfig.zone!!.heating!!.id) ) }
        assertEquals(Unit, result)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/presenceLock")
    @Order(30)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun putPresenceLock() {
        val result = assertCorrectResponse { tadoStrictHomeControlAPI.setPresenceLock(tadoConfig.home!!.id, PresenceLock(HomePresence.HOME) ) }
        assertEquals(Unit, result)
    }

    @Test
    @DisplayName("DEL /homes/{homeId}/presenceLock")
    @Order(31)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun deletePresenceLock() {
        // 422 is expected as this endpoint requires an active Auto-Assist subscription which is not the case
        // for the home we are testing with
        assertHttpErrorStatus(HttpStatus.UNPROCESSABLE_ENTITY) { assertCorrectResponse { tadoStrictHomeControlAPI.deletePresenceLock(tadoConfig.home!!.id ) } }
    }

    @Test
    @DisplayName("GET /homes/{homeId}/state")
    @Order(40)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getHomeState() {
        val endpoint = "GET /homes/{homeId}/state"
        val homeState = assertCorrectResponse { tadoStrictHomeControlAPI.getHomeState(tadoConfig.home!!.id) }
        assertNotNull(homeState)
        verifyHomeState(homeState, endpoint)
    }
}