package tadoclient.apis

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClient
import tadoclient.Application
import tadoclient.models.*
import tadoclient.verify.assertNoHttpErrorStatus
import tadoclient.verify.verifyHomeState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - home control")
class HomeControlApi_IT(
    @Qualifier("tadoRestClient")
    val tadoRestClient: RestClient,

    @Value("\${tado.home.id:-1}")
    val homeId: Long,

    @Value("\${tado.zone.heating.id:-1}")
    val heatingZoneId: Int,
) {
    val tadoHomeControlAPI = HomeControlApi(tadoRestClient)
    val tadoZoneControlAPI = ZoneControlApi(tadoRestClient)

    var zoneOverlaysBeforeTest: MutableMap<Int, ZoneOverlay?> = mutableMapOf()
    var homeStateBeforeTest: HomeState? = null

    // https://stackoverflow.com/questions/51612019/kotlin-how-to-manage-beforeclass-static-method-in-springboottest

    // capture the overlay status before we start running the tests
    @BeforeAll
    fun before()  = try {
        zoneOverlaysBeforeTest[heatingZoneId] = tadoZoneControlAPI.getZoneOverlay(homeId, heatingZoneId)
        homeStateBeforeTest = tadoHomeControlAPI.getHomeState(homeId)
    } catch (e: Exception) {
        // ignore
    }

    @AfterAll()
    fun after() {
        // ZoneOverlay
        // 1. delete
        try {
            tadoZoneControlAPI.deleteZoneOverlay(homeId, heatingZoneId)
        } catch (e: Exception) {
            // ignore
        }
        // 2. recreate
        if (zoneOverlaysBeforeTest[heatingZoneId] != null) {
            val beforeOverlay = zoneOverlaysBeforeTest[heatingZoneId]
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
            tadoZoneControlAPI.setZoneOverlay(HOME_ID, ZONE_ID, newZoneOverlay)
        }

        // presence in home
        homeStateBeforeTest?.let {
            if (it.presenceLocked == true) {
                tadoHomeControlAPI.setPresenceLock(homeId, PresenceLock(it.presence))
            }
        }

    }

    @Test
    @DisplayName("POST /homes/{homeId}/overlay")
    @Order(10)
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
        val result = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoHomeControlAPI.setZoneOverlays(homeId, ZoneOverlays(listOf(ZoneOverlaysOverlaysInner(heatingZoneId.toString(), newZoneOverlay))) )
        }
        assertEquals(Unit, result)
    }

    @Test
    @DisplayName("DELETE /homes/{homeId}/overlay")
    @Order(20)
    fun deleteZoneOverlays() {
        val result = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoHomeControlAPI.deleteZoneOverlays(homeId, rooms = listOf(heatingZoneId) )
        }
        assertEquals(Unit, result)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/presenceLock")
    @Order(30)
    fun putPresenceLock() {
        val result = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoHomeControlAPI.setPresenceLock(homeId, PresenceLock(HomePresence.HOME) )
        }
        assertEquals(Unit, result)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/state")
    @Order(40)
    fun getHomeState() {
        val endpoint = "GET /homes/{homeId}/state"
        val homeState = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoHomeControlAPI.getHomeState(HOME_ID)
        }
        assertNotNull(homeState)
        verifyHomeState(homeState, endpoint)
    }
}