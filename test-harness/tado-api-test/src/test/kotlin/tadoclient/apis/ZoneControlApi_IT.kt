package tadoclient.apis

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClient
import tadoclient.Application
import tadoclient.models.*
import tadoclient.verify.*
import kotlin.test.*
import kotlin.test.Test

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - zone control")
class ZoneControlApi_IT(
    @Qualifier("tadoRestClient")
    val tadoRestClient: RestClient,

    @Value("\${tado.home.id:-1}")
    val homeId: Long,

    @Value("\${tado.zone.heating.id:-1}")
    val heatingZoneId: Int,

    @Value("\${tado.zone.hot-water.id:-1}")
    val hotWaterZoneId: Long,

    @Value("\${tado.zone.air-con.id:-1}")
    val airConZoneId: Long,

    @Value("\${tado.zone.heating.can-set-temperature:false}")
    val heatingZoneCanSetTemperature: Boolean
) {
    val tadoZoneControlAPI = ZoneControlApi(tadoRestClient)


    private var defaultZoneOverlayBeforeTest: DefaultZoneOverlay? = null
    private var earlyStartBeforeTest: EarlyStart? = null
    private var zoneOverlayBeforeTest: ZoneOverlay? = null
    private var zoneAwayConfigurationBeforeTest: ZoneAwayConfiguration? = null
    private var activeTimetableTypeBeforeStart: TimetableType? = null


    // https://stackoverflow.com/questions/51612019/kotlin-how-to-manage-beforeclass-static-method-in-springboottest

    // capture the overlay status before we start running the tests
    @BeforeAll
    fun before()  = try {
        defaultZoneOverlayBeforeTest = tadoZoneControlAPI.getDefaultZoneOverlay(homeId, heatingZoneId)
        earlyStartBeforeTest = tadoZoneControlAPI.getEarlyStart(homeId, heatingZoneId)
        zoneOverlayBeforeTest = tadoZoneControlAPI.getZoneOverlay(homeId, heatingZoneId)
        zoneAwayConfigurationBeforeTest = tadoZoneControlAPI.getAwayConfiguration(homeId, heatingZoneId)
        activeTimetableTypeBeforeStart = tadoZoneControlAPI.getActiveTimetableType(homeId, heatingZoneId)
    } catch (e: Exception) {
        // ignore
    }

    // reset the overlay status to the state it had before we started running the tests
    @AfterAll()
    fun after() {
        // EarlyStart
        earlyStartBeforeTest?.let {
            tadoZoneControlAPI.setEarlyStart(homeId, heatingZoneId, earlyStartBeforeTest!!)
        }

        // ZoneOverlay
        // 1. delete
        try {
            tadoZoneControlAPI.deleteZoneOverlay(homeId, heatingZoneId)
        } catch (e: Exception) {
            // ignore
        }
        // 2. recreate original overlay
        if (zoneOverlayBeforeTest != null) {
            val newZoneOverlay = ZoneOverlay(
                setting = ZoneSetting(
                    type = zoneOverlayBeforeTest?.setting?.type,
                    power = zoneOverlayBeforeTest?.setting?.power,
                    temperature = if (zoneOverlayBeforeTest?.setting?.power == Power.ON) Temperature(celsius = zoneOverlayBeforeTest?.setting?.temperature?.celsius) else null
                ),
                termination = ZoneOverlayTermination(
                    typeSkillBasedApp = zoneOverlayBeforeTest?.termination?.typeSkillBasedApp,
                    durationInSeconds = if (zoneOverlayBeforeTest?.termination?.typeSkillBasedApp == ZoneOverlayTerminationTypeSkillBasedApp.TIMER) zoneOverlayBeforeTest?.termination?.remainingTimeInSeconds else null
                )
            )
            tadoZoneControlAPI.setZoneOverlay(HOME_ID, ZONE_ID, newZoneOverlay)
        }

        // recreate DefaultZoneOverlay
        defaultZoneOverlayBeforeTest?.let {
            val originalDefaultOverlay = DefaultZoneOverlay(
                terminationCondition = DefaultZoneOverlayTerminationCondition(
                    type = defaultZoneOverlayBeforeTest?.terminationCondition?.type,
                    durationInSeconds = defaultZoneOverlayBeforeTest?.terminationCondition?.durationInSeconds
                )
            )
            tadoZoneControlAPI.setDefaultZoneOverlay(HOME_ID, ZONE_ID, originalDefaultOverlay)
        }

        // recreate ZoneAwayConfiguration
        zoneAwayConfigurationBeforeTest?.let {
            tadoZoneControlAPI.setAwayConfiguration(homeId, heatingZoneId, zoneAwayConfigurationBeforeTest!!)
        }

        // reset active timetable type
        activeTimetableTypeBeforeStart?.let {
            tadoZoneControlAPI.setActiveTimetableType(homeId, heatingZoneId, activeTimetableTypeBeforeStart!!)
        }
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/earlyStart")
    @Order(10)
    fun getEarlyStart() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/earlyStart"
        val earlyStart = tadoZoneControlAPI.getEarlyStart(homeId, heatingZoneId)
        val typeName = "EarlyStart"
        verifyNested(earlyStart, endpoint, typeName, typeName)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/earlyStart")
    @Order(20)
    fun putEarlyStart() {
        val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/earlyStart"
        val earlyStart = tadoZoneControlAPI.setEarlyStart(homeId, heatingZoneId, EarlyStart(enabled = false))
        val typeName = "EarlyStart"
        verifyNested(earlyStart, endpoint, typeName, typeName)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/defaultOverlay")
    @Order(30)
    fun getDefaultZoneOverlay() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/defaultOverlay"
        val defaultZoneOverlay = tadoZoneControlAPI.getDefaultZoneOverlay(homeId, heatingZoneId)
        val typeName = "DefaultZoneOverlay"
        verifyNested(defaultZoneOverlay, endpoint, typeName, typeName,
            nullAllowedProperties = listOf("$typeName.terminationCondition.durationInSeconds"))
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/defaultOverlay")
    @Order(40)
    fun putDefaultZoneOverlay() {
        val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/defaultOverlay"
        val newDefaultZoneOverlay = DefaultZoneOverlay(
            terminationCondition = DefaultZoneOverlayTerminationCondition(
                type = ZoneOverlayTerminationType.TADO_MODE
            )
        )
        val defaultZoneOverlay = tadoZoneControlAPI.setDefaultZoneOverlay(homeId, heatingZoneId, newDefaultZoneOverlay)
        val typeName = "DefaultZoneOverlay"
        verifyNested(defaultZoneOverlay, endpoint, typeName, typeName,
            nullAllowedProperties = listOf("$typeName.terminationCondition.durationInSeconds"))
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/overlay")
    @Order(50)
    fun getZoneOverlay() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/overlay"

        // first make sure there is an overlay, otherwise 404 will be returned
        val zoneOverlay1 = ZoneOverlay(
            setting = ZoneSetting(
                type = ZoneType.HEATING,
                power = Power.OFF),
            termination = ZoneOverlayTermination(typeSkillBasedApp = ZoneOverlayTerminationTypeSkillBasedApp.MANUAL)
        )
        tadoZoneControlAPI.setZoneOverlay(homeId, heatingZoneId, zoneOverlay1)

        // now we can test
        val zoneOverlay = tadoZoneControlAPI.getZoneOverlay(homeId, heatingZoneId)
        verifyZoneOverlay(Pair(ZoneType.HEATING, true), zoneOverlay, endpoint)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/overlay - 404 no overlay set")
    @Order(51)
    fun getZoneOverlay_404_noOverlay() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/overlay"
        // first make sure there is no overlay
        tadoZoneControlAPI.deleteZoneOverlay(homeId, heatingZoneId)

        // now we can test
        assertHttpErrorStatus(HttpStatus.NOT_FOUND) {
            tadoZoneControlAPI.getZoneOverlay(homeId, heatingZoneId)
        }
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/overlay - 404 unknown zone")
    @Order(51)
    fun getZoneOverlay_404_unknownZone() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/overlay"
        // now we can test
        assertHttpErrorStatus(HttpStatus.NOT_FOUND) {
            tadoZoneControlAPI.getZoneOverlay(homeId, 99999)
        }
    }

    // TODO: assertNo403 for all test cases

    // Result:
    // Overlay(type=MANUAL, setting=ZoneSetting(power=OFF, type=HEATING, temperature=null),
    // termination=OverlayTermination(type=MANUAL, durationInSeconds=null, remainingTimeInSeconds=null, ZoneOverlayTerminiationTypeSkillBasedApp=MANUAL, expiry=null, projectedExpiry=null))
    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/overlay - MANUAL OFF")
    @Order(60)
    fun setZoneOverlay_manual_off() {
        val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/overlay"
        // first delete any existing overlay
        tadoZoneControlAPI.deleteZoneOverlay(homeId, heatingZoneId)

        // set the overlay
        val zoneOverlay = ZoneOverlay(
            setting = ZoneSetting(
                type = ZoneType.HEATING,
                power = Power.OFF),
            termination = ZoneOverlayTermination(typeSkillBasedApp = ZoneOverlayTerminationTypeSkillBasedApp.MANUAL)
        )
        val result = tadoZoneControlAPI.setZoneOverlay(homeId, heatingZoneId, zoneOverlay)
        verifyZoneOverlay(Pair(ZoneType.HEATING, true), result, endpoint)
    }

    // result:
    // ZoneOverlay(type=MANUAL, setting=ZoneSetting(power=ON, type=HEATING, temperature=ZoneSettingTemperature(celsius=18.8, fahrenheit=65.84)),
    // termination=ZoneOverlayTermination(type=MANUAL, durationInSeconds=null, remainingTimeInSeconds=null, ZoneOverlayTerminiationTypeSkillBasedApp=MANUAL, expiry=null, projectedExpiry=null))
    // Text in app: Until you resume schedule
    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/overlay - MANUAL ON")
    @Order(61)
    fun setZoneOverlay_manual_on() {
        val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/overlay"
        // first delete any existing overlay
        tadoZoneControlAPI.deleteZoneOverlay(homeId, heatingZoneId)

        // set the overlay
        val zoneOverlay = ZoneOverlay(
            setting = ZoneSetting(
                type = ZoneType.HEATING,
                power = Power.ON,
                temperature = Temperature(celsius = 18.8f)
            ),
            termination = ZoneOverlayTermination(typeSkillBasedApp = ZoneOverlayTerminationTypeSkillBasedApp.MANUAL)
        )
        val result = tadoZoneControlAPI.setZoneOverlay(homeId, heatingZoneId, zoneOverlay)
        verifyZoneOverlay(Pair(ZoneType.HEATING, true), result, endpoint)
    }



    // Result:
    // ZoneOverlay(type=MANUAL, setting=ZoneSetting(power=ON, type=HEATING, temperature=ZoneSettingTemperature(celsius=18.8, fahrenheit=65.84)),
    // termination=ZoneOverlayTermination(type=TADO_MODE, durationInSeconds=null, remainingTimeInSeconds=null, ZoneOverlayTerminiationTypeSkillBasedApp=TADO_MODE, expiry=null, projectedExpiry=null))
    // Text in app: Active indefinitely

    // Type tado_mode set when temperature set via thermostat.
    // Seems to set the projected expiry and that is probably because of the configured defaultOverlay
    // Text in app: Until 10:25 PM while in Home Mode
    // overlay: {
    //                "type": "MANUAL",
    //                "setting": {
    //                    "type": "HEATING",
    //                    "power": "ON",
    //                    "temperature": {
    //                        "celsius": 6.00,
    //                        "fahrenheit": 42.80
    //                    }
    //                },
    //                "termination": {
    //                    "type": "TADO_MODE",
    //                    "ZoneOverlayTerminiationTypeSkillBasedApp": "TADO_MODE",
    //                    "projectedExpiry": "2024-08-16T20:25:00Z"
    //                }
    //            }
    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/overlay - TADO_MODE ON")
    @Order(62)
    fun setZoneOverlay_tado_mode() {
        val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/overlay"
        // first delete any existing overlay
        tadoZoneControlAPI.deleteZoneOverlay(homeId, heatingZoneId)

        // set the overlay
        val zoneOverlay = ZoneOverlay(
            setting = ZoneSetting(
                type = ZoneType.HEATING,
                power = Power.ON,
                temperature = Temperature(celsius = 18.8f)
            ),
            termination = ZoneOverlayTermination(typeSkillBasedApp = ZoneOverlayTerminationTypeSkillBasedApp.TADO_MODE)
        )
        val result = tadoZoneControlAPI.setZoneOverlay(homeId, heatingZoneId, zoneOverlay)
        verifyZoneOverlay(Pair(ZoneType.HEATING, true), result, endpoint)
    }


    // result:
    // ZoneOverlay(type=MANUAL, setting=ZoneSetting(power=ON, type=HEATING, temperature=ZoneSettingTemperature(celsius=18.8, fahrenheit=65.84)),
    // termination=ZoneOverlayTermination(type=TIMER, durationInSeconds=1800, remainingTimeInSeconds=2033, ZoneOverlayTerminiationTypeSkillBasedApp=NEXT_TIME_BLOCK, expiry=2024-08-14T20:00:00Z, projectedExpiry=2024-08-14T20:00:00Z))
    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/overlay - NEXT_TIME_BLOCK ON")
    @Order(63)
    fun setZoneOverlay_nextTimeBlock() {
        val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/overlay"
        // first delete any existing overlay
        tadoZoneControlAPI.deleteZoneOverlay(homeId, heatingZoneId)

        // set the overlay
        val zoneOverlay = ZoneOverlay(
            setting = ZoneSetting(
                type = ZoneType.HEATING,
                power = Power.ON,
                temperature = Temperature(celsius = 18.8f)
            ),
            termination = ZoneOverlayTermination(typeSkillBasedApp = ZoneOverlayTerminationTypeSkillBasedApp.NEXT_TIME_BLOCK)
        )
        val result = tadoZoneControlAPI.setZoneOverlay(homeId, heatingZoneId, zoneOverlay)
        verifyZoneOverlay(Pair(ZoneType.HEATING, true), result, endpoint)
    }

    // Result:
    // ZoneOverlay(type=MANUAL, setting=ZoneSetting(power=ON, type=HEATING, temperature=ZoneSettingTemperature(celsius=18.8, fahrenheit=65.84)),
    // termination=ZoneOverlayTermination(type=TIMER, durationInSeconds=1400, remainingTimeInSeconds=1399, ZoneOverlayTerminiationTypeSkillBasedApp=TIMER, expiry=2024-08-14T19:49:24Z, projectedExpiry=2024-08-14T19:49:24Z))
    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/overlay - TIMER ON")
    @Order(64)
    fun setZoneOverlay_timer() {
        val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/overlay"
        // first delete any existing overlay
        tadoZoneControlAPI.deleteZoneOverlay(homeId, heatingZoneId)

        // set the overlay
        val zoneOverlay = ZoneOverlay(
            setting = ZoneSetting(
                type = ZoneType.HEATING,
                power = Power.ON,
                temperature = Temperature(celsius = 18.8f)
            ),
            termination = ZoneOverlayTermination(
                typeSkillBasedApp = ZoneOverlayTerminationTypeSkillBasedApp.TIMER,
                durationInSeconds = 1000
            )
        )
        val result = tadoZoneControlAPI.setZoneOverlay(homeId, heatingZoneId, zoneOverlay)
        verifyZoneOverlay(Pair(ZoneType.HEATING, true), result, endpoint)
    }

    @Test
    @DisplayName("DELETE /homes/{homeId}/zones/{zoneId}/overlay")
    @Order(70)
    fun deleteZoneOverlay() {
        val result = tadoZoneControlAPI.deleteZoneOverlay(homeId, heatingZoneId)
        assertEquals(Unit, result)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/schedule/awayConfiguration - HEATING")
    @Order(80)
    fun getAwayConfiguration() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/schedule/awayConfiguration"
        val awayConfiguration = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoZoneControlAPI.getAwayConfiguration(homeId, heatingZoneId)
        }
        assertNotNull(awayConfiguration)
        verifyZoneAwayConfiguration(Pair(ZoneType.HEATING, true), awayConfiguration, endpoint)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/schedule/awayConfiguration")
    @Order(90)
    fun setAwayConfiguration() {
        val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/schedule/awayConfiguration"
        val input = ZoneAwayConfiguration(
            type = ZoneType.HEATING,
            autoAdjust = false,
            setting = ZoneSetting(
                type = ZoneType.HEATING,
                power = Power.OFF
            )
        )
        val response = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoZoneControlAPI.setAwayConfiguration(homeId, heatingZoneId, input)
        }
        assertEquals(Unit, response)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/schedule/activeTimetable - HEATING")
    @Order(100)
    fun getActiveTimetableType() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/schedule/activeTimetable"
        val timetableType = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoZoneControlAPI.getActiveTimetableType(homeId, heatingZoneId)
        }
        verifyTimetableType(timetableType, endpoint)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/schedule/activeTimetable - HEATING")
    @Order(110)
    fun setActiveTimetableType() {
        val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/schedule/activeTimetable"
        val activeTimeTableType = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoZoneControlAPI.setActiveTimetableType(homeId, heatingZoneId, TimetableType(id = TimetableTypeId._1))
        }
        verifyTimetableType(activeTimeTableType, endpoint)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/schedule/timetables - HEATING")
    @Order(120)
    fun getTimetables() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/schedule/timetables"
        val timetables = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoZoneControlAPI.getZoneTimetables(homeId, heatingZoneId)
        }
        assertNotEquals(0, timetables.size)
        verifyTimetableType(timetables[0], endpoint)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/schedule/timetables/{timetableTypeId} - HEATING")
    @Order(130)
    fun getTimetable() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/schedule/timetables/{timetableTypeId}"
        val timetable = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoZoneControlAPI.getZoneTimetable(homeId, heatingZoneId, 1)
        }
        assertNotNull(timetable)
        verifyTimetableType(timetable, endpoint)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/schedule/timetables/{timetableTypeId}/blocks - HEATING")
    @Order(140)
    fun getTimetableBlocks() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/schedule/timetables/{timetableTypeId}/blocks"
        val timetableBlocks = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoZoneControlAPI.getZoneTimetableBlocks(homeId, heatingZoneId, TimetableTypeId._1)
        }
        assertNotNull(timetableBlocks)
        assertNotEquals(0, timetableBlocks.size)
        verifyTimetableBlock(Pair(ZoneType.HEATING, true), timetableBlocks[0], endpoint)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/schedule/timetables/{timetableTypeId}/blocks/{dayType} - HEATING")
    @Order(150)
    fun getTimetableBlocksByDayType() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/schedule/timetables/{timetableTypeId}/blocks/{dayType}"
        val timetableBlocks = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoZoneControlAPI.getTimetableBlocksByDayType(homeId, heatingZoneId, TimetableTypeId._1, DayType.SATURDAY)
        }
        assertNotNull(timetableBlocks)
        assertNotEquals(0, timetableBlocks.size)
        verifyTimetableBlock(Pair(ZoneType.HEATING, true), timetableBlocks[0], endpoint)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/schedule/timetables/{timetableTypeId}/blocks/{dayType} - HEATING")
    @Order(160)
    @Disabled("returns 500 Internal Server Error (\"Please contact customer support\")")
    fun setTimetableBlocksByDayType() {
        val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/schedule/timetables/{timetableTypeId}/blocks/{dayType}"
        // GET the current timetable...
        val timetableBlocks = tadoZoneControlAPI.getTimetableBlocksByDayType(homeId, heatingZoneId, TimetableTypeId._1, DayType.SATURDAY)

        // ... and use that as the input for the PUT call
        // returned 500 Internal Server Error: "Please contact customer support"
        val response = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoZoneControlAPI.setTimetableBlocksForDayType(homeId, heatingZoneId, TimetableTypeId._1, DayType.SATURDAY, timetableBlocks)
        }
        assertNotNull(timetableBlocks)
        assertNotEquals(0, response.size)
        verifyTimetableBlock(Pair(ZoneType.HEATING, true), response[0], endpoint)
    }

}