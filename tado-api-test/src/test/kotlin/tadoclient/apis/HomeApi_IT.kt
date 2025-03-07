package tadoclient.apis

import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledIf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestClient
import tadoclient.Application
import tadoclient.TadoConfig
import tadoclient.models.*
import tadoclient.verify.*
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - home")
class HomeApi_IT(
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
    val tadoHomeAPI = HomeApi(tadoRestClient)
    val tadoStrictHomeAPI = HomeApi(tadoStrictRestClient)

    var heatingSystemBeforeTest: HeatingSystem? = null

    @BeforeAll
    fun beforeTest() {
        heatingSystemBeforeTest = tadoHomeAPI.getHeatingSystem(tadoConfig.home!!.id)
    }

    @AfterAll
    fun afterTests() {
        // boiler and underfloorHeating
        heatingSystemBeforeTest?.let{
            val boiler = Boiler1(
                present = heatingSystemBeforeTest!!.boiler!!.present,
                id = heatingSystemBeforeTest!!.boiler?.id
            )
            tadoHomeAPI.setBoiler(tadoConfig.home!!.id, boiler)
            tadoHomeAPI.setUnderfloorHeating(tadoConfig.home!!.id, UnderfloorHeating(present = heatingSystemBeforeTest!!.underfloorHeating!!.present))
        }
    }

    @Test
    @DisplayName("GET /homes/{homeId}")
    @Order(10)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getHome() {
        val endpoint = "GET /homes/{homeId}"
        val home = assertCorrectResponse { tadoStrictHomeAPI.getHome(tadoConfig.home!!.id) }
        verifyHome(home, endpoint)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/airComfort")
    @Order(20)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getAirComfort() {
        val endpoint = "GET /homes/{homeId}/airComfort"
        val airComfort = assertCorrectResponse { tadoStrictHomeAPI.getAirComfort(tadoConfig.home!!.id) }
        verifyAirComfort(airComfort, endpoint)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/awayRadiusInMeters")
    @Order(25)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun putAwayRadiusInMeters() {
        // first get the current value
        val currentRadius = tadoHomeAPI.getHome(tadoConfig.home!!.id).awayRadiusInMeters
        // then do the actual test
        tadoStrictHomeAPI.setAwayRadiusInMeters(tadoConfig.home.id, AwayRadiusInput(currentRadius))
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/details")
    @Order(30)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun putDetails() {
        // first get the current home details
        val home = tadoHomeAPI.getHome(tadoConfig.home!!.id)
        // either add or remove an 'x' to/from the end of the name of the home
        val newName = home.name?.let{
            if (home.name!!.get(home.name!!.length-1) == 'x') {
                home.name!!.substring(0, home.name!!.length-1)
            } else {
                home.name + "x"
            }
        }
        val homeDetails = HomeDetails(
            name = newName,
            contactDetails = home.contactDetails,
            address = home.address,
            geolocation = home.geolocation)
        assertCorrectResponse { tadoStrictHomeAPI.setHomeDetails(tadoConfig.home!!.id, homeDetails) }
    }

    @Test
    @DisplayName("GET /homes/{homeId}/flowTemperatureOptimization")
    @Order(40)
    @EnabledIf(value = "isBoilerOpenThermInterface", disabledReason = "boiler does not support OPENTHERM")
    fun getFlowTemperaturOptimization() {
        val endpoint = "GET /homes/{homeId}/flowTemperatureOptimization"
        val flowTempOptimization = assertCorrectResponse { tadoStrictHomeAPI.getFlowTemperatureOptimization(tadoConfig.home!!.id) }
        val typeName = "FlowTemperatureOptimization"
        verifyObject(flowTempOptimization, endpoint, typeName, typeName,
            nullAllowedProperties = listOf("$typeName.autoAdaptation.maxFlowTemperature")
        )
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/flowTemperatureOptimization")
    @Order(41)
    @Disabled("Unsuitable for weekly automated test execution: not making any changes to the boiler") // and boiler also needs to support OPENTHERM
    fun putFlowTemperaturOptimization() {
        val input = FlowTemperatureOptimizationInput(BigDecimal(50.5))
        tadoStrictHomeAPI.setFlowTemperatureOptimization(tadoConfig.home!!.id, input)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/heatingSystem")
    @Order(50)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getHeatingSystem() {
        val endpoint = "GET /homes/{homeId}/heatingSystem"
        val heatingSystem = assertCorrectResponse { tadoStrictHomeAPI.getHeatingSystem(tadoConfig.home!!.id) }
        verifyHeatingSystem(heatingSystem, endpoint)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/heatingSystem/boiler - not present")
    @Order(60)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun putBoiler_NotPresent() {
        val boiler = Boiler1(present = false)
        assertCorrectResponse { tadoStrictHomeAPI.setBoiler(tadoConfig.home!!.id, boiler) }
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/heatingSystem/boiler - present without id")
    @Order(61)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun putBoiler_PresentWithoutId() {
        val boiler = Boiler1(present = true)
        assertCorrectResponse { tadoStrictHomeAPI.setBoiler(tadoConfig.home!!.id, boiler) }
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/heatingSystem/boiler - present with id")
    @Order(62)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun putBoiler_PresentWithId() {
        val boiler = Boiler1(present = true, id = 2699)
        assertCorrectResponse { tadoStrictHomeAPI.setBoiler(tadoConfig.home!!.id, boiler) }
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/heatingSystem/underfloorHeating")
    @Order(70)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun putUnderfloorHeating() {
        val underfloorHeating = UnderfloorHeating(present = false)
        val response = assertCorrectResponse { tadoStrictHomeAPI.setUnderfloorHeating(tadoConfig.home!!.id, underfloorHeating) }
        assertEquals(Unit, response)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/incidentDetection")
    @Order(80)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getIncidentDetection() {
        val endpoint = "GET /homes/{homeId}/incidentDetection"
        val incidentDetection = assertCorrectResponse { tadoStrictHomeAPI.getIncidentDetection(tadoConfig.home!!.id) }
        val typeName = "IncidentDetection"
        verifyObject(incidentDetection, endpoint, typeName, typeName)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/incidentDetection")
    @Order(90)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun putIncidentDetection() {
        assertCorrectResponse { tadoStrictHomeAPI.setIncidentDetection(tadoConfig.home!!.id, IncidentDetectionInput(enabled = true)) }
    }

    @Test
    @DisplayName("GET /homes/{homeId}/weather")
    @Order(100)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getWeather() {
        val endpoint = "GET /homes/{homeId}/weather"
        val weather = assertCorrectResponse { tadoStrictHomeAPI.getWeather(tadoConfig.home!!.id) }
        verifyWeather(weather, endpoint)
    }
}