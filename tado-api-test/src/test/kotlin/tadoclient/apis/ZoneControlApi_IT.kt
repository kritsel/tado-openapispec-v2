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
import tadoclient.verify.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - zone control")
class ZoneControlApi_IT(
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
    val tadoZoneControlAPI = ZoneControlApi(tadoRestClient)
    val tadoStrictZoneControlAPI = ZoneControlApi(tadoStrictRestClient)


    private var defaultZoneOverlayBeforeTest: DefaultZoneOverlay? = null
    private var earlyStartBeforeTest: EarlyStart? = null
    private var zoneOverlayBeforeTest: ZoneOverlay? = null
    private var zoneAwayConfigurationBeforeTest: ZoneAwayConfiguration? = null
    private var activeTimetableTypeBeforeStart: TimetableType? = null


    // https://stackoverflow.com/questions/51612019/kotlin-how-to-manage-beforeclass-static-method-in-springboottest

    // capture the overlay status before we start running the tests
    // TODO: conditionally on presence of set-up
    @BeforeAll
    fun before()  = try {
        defaultZoneOverlayBeforeTest = tadoZoneControlAPI.getDefaultZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)
        earlyStartBeforeTest = tadoZoneControlAPI.getEarlyStart(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)
        zoneOverlayBeforeTest = tadoZoneControlAPI.getZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)
        zoneAwayConfigurationBeforeTest = tadoZoneControlAPI.getAwayConfiguration(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)
        activeTimetableTypeBeforeStart = tadoZoneControlAPI.getActiveTimetableType(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)
    } catch (e: Exception) {
        // ignore
    }

    // reset the overlay status to the state it had before we started running the tests
    // TODO: conditionally on presence of set-up
    @AfterAll()
    fun after() {
        // EarlyStart
        earlyStartBeforeTest?.let {
            tadoZoneControlAPI.setEarlyStart(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, earlyStartBeforeTest!!)
        }

        // ZoneOverlay
        // 1. delete
        try {
            tadoZoneControlAPI.deleteZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)
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
            tadoZoneControlAPI.setZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, newZoneOverlay)
        }

        // recreate DefaultZoneOverlay
        defaultZoneOverlayBeforeTest?.let {
            val originalDefaultOverlay = DefaultZoneOverlay(
                terminationCondition = DefaultZoneOverlayTerminationCondition(
                    type = defaultZoneOverlayBeforeTest?.terminationCondition?.type,
                    durationInSeconds = defaultZoneOverlayBeforeTest?.terminationCondition?.durationInSeconds
                )
            )
            tadoZoneControlAPI.setDefaultZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, originalDefaultOverlay)
        }

        // recreate ZoneAwayConfiguration
        zoneAwayConfigurationBeforeTest?.let {
            tadoZoneControlAPI.setAwayConfiguration(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, zoneAwayConfigurationBeforeTest!!)
        }

        // reset active timetable type
        activeTimetableTypeBeforeStart?.let {
            tadoZoneControlAPI.setActiveTimetableType(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, activeTimetableTypeBeforeStart!!)
        }
    }

    @Test
    @DisplayName("POST /homes/{homeId}/overlay")
    @Order(4)
//    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    @Disabled("needs additional test set-up to revert back to the pre-test situation")
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
            tadoStrictZoneControlAPI.setZoneOverlays(tadoConfig.home!!.id, ZoneOverlays(listOf(ZoneOverlaysOverlaysInner(tadoConfig.zone!!.heating!!.id.toString(), newZoneOverlay))) )
        }
        assertEquals(Unit, result)
    }

    @Test
    @DisplayName("DELETE /homes/{homeId}/overlay")
    @Order(7)
//    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    @Disabled("needs additional test set-up to revert back to the pre-test situation")
    fun deleteZoneOverlays() {
        val result = assertCorrectResponse { tadoStrictZoneControlAPI.deleteZoneOverlays(tadoConfig.home!!.id, rooms = listOf(tadoConfig.zone!!.heating!!.id) ) }
        assertEquals(Unit, result)
    }


    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/earlyStart")
    @Order(10)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun getEarlyStart() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/earlyStart"
        val earlyStart = assertCorrectResponse { tadoStrictZoneControlAPI.getEarlyStart(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id) }
        val typeName = "EarlyStart"
        verifyNested(earlyStart, endpoint, typeName, typeName)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/earlyStart")
    @Order(20)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun putEarlyStart() {
        val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/earlyStart"
        val earlyStart = assertCorrectResponse {
            tadoStrictZoneControlAPI.setEarlyStart(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, EarlyStart(enabled = false))
        }
        val typeName = "EarlyStart"
        verifyNested(earlyStart, endpoint, typeName, typeName)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/defaultOverlay")
    @Order(30)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun getDefaultZoneOverlay() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/defaultOverlay"
        val defaultZoneOverlay = assertCorrectResponse {
            tadoStrictZoneControlAPI.getDefaultZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)
        }
        val typeName = "DefaultZoneOverlay"
        verifyNested(defaultZoneOverlay, endpoint, typeName, typeName,
            nullAllowedProperties = listOf("$typeName.terminationCondition.durationInSeconds"))
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/defaultOverlay")
    @Order(40)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun putDefaultZoneOverlay() {
        val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/defaultOverlay"
        val newDefaultZoneOverlay = DefaultZoneOverlay(
            terminationCondition = DefaultZoneOverlayTerminationCondition(
                type = ZoneOverlayTerminationType.TADO_MODE
            )
        )
        val defaultZoneOverlay = assertCorrectResponse {
            tadoStrictZoneControlAPI.setDefaultZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, newDefaultZoneOverlay)
        }
        val typeName = "DefaultZoneOverlay"
        verifyNested(defaultZoneOverlay, endpoint, typeName, typeName,
            nullAllowedProperties = listOf("$typeName.terminationCondition.durationInSeconds"))
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/overlay")
    @Order(50)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun getZoneOverlay() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/overlay"

        // first make sure there is an overlay, otherwise 404 will be returned
        val zoneOverlay1 = ZoneOverlay(
            setting = ZoneSetting(
                type = ZoneType.HEATING,
                power = Power.OFF),
            termination = ZoneOverlayTermination(typeSkillBasedApp = ZoneOverlayTerminationTypeSkillBasedApp.MANUAL)
        )
        tadoStrictZoneControlAPI.setZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, zoneOverlay1)

        // now we can test
        val zoneOverlay = assertCorrectResponse { tadoStrictZoneControlAPI.getZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id) }
        verifyZoneOverlay(Pair(ZoneType.HEATING, true), zoneOverlay, endpoint)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/overlay - 404 no overlay set")
    @Order(51)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun getZoneOverlay_404_noOverlay() {
        // first make sure there is no overlay
        tadoStrictZoneControlAPI.deleteZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)

        // now we can test
        assertHttpErrorStatus(HttpStatus.NOT_FOUND) {
            tadoStrictZoneControlAPI.getZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)
        }
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/overlay - 404 unknown zone")
    @Order(51)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getZoneOverlay_404_unknownZone() {
        assertHttpErrorStatus(HttpStatus.NOT_FOUND)  {
            tadoStrictZoneControlAPI.getZoneOverlay(tadoConfig.home!!.id, 99999)
        }
    }

    // Result:
    // Overlay(type=MANUAL, setting=ZoneSetting(power=OFF, type=HEATING, temperature=null),
    // termination=OverlayTermination(type=MANUAL, durationInSeconds=null, remainingTimeInSeconds=null, ZoneOverlayTerminiationTypeSkillBasedApp=MANUAL, expiry=null, projectedExpiry=null))
    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/overlay - MANUAL OFF")
    @Order(60)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun setZoneOverlay_manual_off() {
        val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/overlay"
        // first delete any existing overlay
        tadoStrictZoneControlAPI.deleteZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)

        // set the overlay
        val zoneOverlay = ZoneOverlay(
            setting = ZoneSetting(
                type = ZoneType.HEATING,
                power = Power.OFF),
            termination = ZoneOverlayTermination(typeSkillBasedApp = ZoneOverlayTerminationTypeSkillBasedApp.MANUAL)
        )
        val result = assertCorrectResponse { tadoStrictZoneControlAPI.setZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, zoneOverlay) }
        verifyZoneOverlay(Pair(ZoneType.HEATING, true), result, endpoint)
    }

    // result:
    // ZoneOverlay(type=MANUAL, setting=ZoneSetting(power=ON, type=HEATING, temperature=ZoneSettingTemperature(celsius=18.8, fahrenheit=65.84)),
    // termination=ZoneOverlayTermination(type=MANUAL, durationInSeconds=null, remainingTimeInSeconds=null, ZoneOverlayTerminiationTypeSkillBasedApp=MANUAL, expiry=null, projectedExpiry=null))
    // Text in app: Until you resume schedule
    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/overlay - MANUAL ON")
    @Order(61)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun setZoneOverlay_manual_on() {
        val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/overlay"
        // first delete any existing overlay
        tadoStrictZoneControlAPI.deleteZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)

        // set the overlay
        val zoneOverlay = ZoneOverlay(
            setting = ZoneSetting(
                type = ZoneType.HEATING,
                power = Power.ON,
                temperature = Temperature(celsius = 18.8f)
            ),
            termination = ZoneOverlayTermination(typeSkillBasedApp = ZoneOverlayTerminationTypeSkillBasedApp.MANUAL)
        )
        val result = assertCorrectResponse { tadoStrictZoneControlAPI.setZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, zoneOverlay) }
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
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun setZoneOverlay_tado_mode() {
        val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/overlay"
        // first delete any existing overlay
        tadoStrictZoneControlAPI.deleteZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)

        // set the overlay
        val zoneOverlay = ZoneOverlay(
            setting = ZoneSetting(
                type = ZoneType.HEATING,
                power = Power.ON,
                temperature = Temperature(celsius = 18.8f)
            ),
            termination = ZoneOverlayTermination(typeSkillBasedApp = ZoneOverlayTerminationTypeSkillBasedApp.TADO_MODE)
        )
        val result = assertCorrectResponse { tadoStrictZoneControlAPI.setZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, zoneOverlay) }
        verifyZoneOverlay(Pair(ZoneType.HEATING, true), result, endpoint)
    }


    // result:
    // ZoneOverlay(type=MANUAL, setting=ZoneSetting(power=ON, type=HEATING, temperature=ZoneSettingTemperature(celsius=18.8, fahrenheit=65.84)),
    // termination=ZoneOverlayTermination(type=TIMER, durationInSeconds=1800, remainingTimeInSeconds=2033, ZoneOverlayTerminiationTypeSkillBasedApp=NEXT_TIME_BLOCK, expiry=2024-08-14T20:00:00Z, projectedExpiry=2024-08-14T20:00:00Z))
    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/overlay - NEXT_TIME_BLOCK ON")
    @Order(63)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun setZoneOverlay_nextTimeBlock() {
        val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/overlay"
        // first delete any existing overlay
        tadoStrictZoneControlAPI.deleteZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)

        // set the overlay
        val zoneOverlay = ZoneOverlay(
            setting = ZoneSetting(
                type = ZoneType.HEATING,
                power = Power.ON,
                temperature = Temperature(celsius = 18.8f)
            ),
            termination = ZoneOverlayTermination(typeSkillBasedApp = ZoneOverlayTerminationTypeSkillBasedApp.NEXT_TIME_BLOCK)
        )
        val result = assertCorrectResponse { tadoStrictZoneControlAPI.setZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, zoneOverlay) }
        verifyZoneOverlay(Pair(ZoneType.HEATING, true), result, endpoint)
    }

    // Result:
    // ZoneOverlay(type=MANUAL, setting=ZoneSetting(power=ON, type=HEATING, temperature=ZoneSettingTemperature(celsius=18.8, fahrenheit=65.84)),
    // termination=ZoneOverlayTermination(type=TIMER, durationInSeconds=1400, remainingTimeInSeconds=1399, ZoneOverlayTerminiationTypeSkillBasedApp=TIMER, expiry=2024-08-14T19:49:24Z, projectedExpiry=2024-08-14T19:49:24Z))
    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/overlay - TIMER ON")
    @Order(64)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun setZoneOverlay_timer() {
        val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/overlay"
        // first delete any existing overlay
        tadoStrictZoneControlAPI.deleteZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)

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
        val result = assertCorrectResponse { tadoStrictZoneControlAPI.setZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, zoneOverlay) }
        verifyZoneOverlay(Pair(ZoneType.HEATING, true), result, endpoint)
    }

    @Test
    @DisplayName("DELETE /homes/{homeId}/zones/{zoneId}/overlay")
    @Order(70)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun deleteZoneOverlay() {
        val result = assertCorrectResponse { tadoStrictZoneControlAPI.deleteZoneOverlay(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id) }
        assertEquals(Unit, result)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/schedule/awayConfiguration - HEATING")
    @Order(80)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun getAwayConfiguration() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/schedule/awayConfiguration"
        val awayConfiguration = assertCorrectResponse { tadoStrictZoneControlAPI.getAwayConfiguration(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id) }
        assertNotNull(awayConfiguration)
        verifyZoneAwayConfiguration(Pair(ZoneType.HEATING, true), awayConfiguration, endpoint)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/schedule/awayConfiguration")
    @Order(90)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun setAwayConfiguration() {
        val input = ZoneAwayConfiguration(
            type = ZoneType.HEATING,
            autoAdjust = false,
            setting = ZoneSetting(
                type = ZoneType.HEATING,
                power = Power.OFF
            )
        )
        assertCorrectResponse { tadoStrictZoneControlAPI.setAwayConfiguration(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, input) }
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/schedule/activeTimetable - HEATING")
    @Order(100)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun getActiveTimetableType() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/schedule/activeTimetable"
        val timetableType = assertCorrectResponse { tadoStrictZoneControlAPI.getActiveTimetableType(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id) }
        verifyTimetableType(timetableType, endpoint)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/zones/{zoneId}/schedule/activeTimetable - HEATING")
    @Order(110)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun setActiveTimetableType() {
        val endpoint = "PUT /homes/{homeId}/zones/{zoneId}/schedule/activeTimetable"
        val activeTimeTableType = assertCorrectResponse {
            tadoStrictZoneControlAPI.setActiveTimetableType(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, TimetableType(id = TimetableTypeId._1))
        }
        verifyTimetableType(activeTimeTableType, endpoint)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/schedule/timetables - HEATING")
    @Order(120)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun getTimetables() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/schedule/timetables"
        val timetables = assertCorrectResponse {
            tadoStrictZoneControlAPI.getZoneTimetables(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id)
        }
        assertNotEquals(0, timetables.size)
        verifyTimetableType(timetables[0], endpoint)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/schedule/timetables/{timetableTypeId} - HEATING")
    @Order(130)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun getTimetable() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/schedule/timetables/{timetableTypeId}"
        val timetable = assertCorrectResponse {
            tadoStrictZoneControlAPI.getZoneTimetable(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, 1)
        }
        assertNotNull(timetable)
        verifyTimetableType(timetable, endpoint)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/schedule/timetables/{timetableTypeId}/blocks - HEATING")
    @Order(140)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun getTimetableBlocks() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/schedule/timetables/{timetableTypeId}/blocks"
        val timetableBlocks = assertCorrectResponse {
            tadoStrictZoneControlAPI.getZoneTimetableBlocks(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, TimetableTypeId._1)
        }
        assertNotNull(timetableBlocks)
        assertNotEquals(0, timetableBlocks.size)
        verifyTimetableBlock(Pair(ZoneType.HEATING, true), timetableBlocks[0], endpoint)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/schedule/timetables/{timetableTypeId}/blocks/{dayType} - HEATING")
    @Order(150)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or HEATING zone specified in tado set-up")
    fun getTimetableBlocksByDayType() {
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/schedule/timetables/{timetableTypeId}/blocks/{dayType}"
        val timetableBlocks = assertCorrectResponse {
            tadoStrictZoneControlAPI.getTimetableBlocksByDayType(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, TimetableTypeId._1, DayType.SATURDAY)
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
        val timetableBlocks = tadoStrictZoneControlAPI.getTimetableBlocksByDayType(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, TimetableTypeId._1, DayType.SATURDAY)

        // ... and use that as the input for the PUT call
        // returned 500 Internal Server Error: "Please contact customer support"
        val response = assertCorrectResponse {
            tadoStrictZoneControlAPI.setTimetableBlocksForDayType(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, TimetableTypeId._1, DayType.SATURDAY, timetableBlocks)
        }
        assertNotNull(timetableBlocks)
        assertNotEquals(0, response.size)
        verifyTimetableBlock(Pair(ZoneType.HEATING, true), response[0], endpoint)
    }
}