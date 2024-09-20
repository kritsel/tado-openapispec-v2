package tadoclient.apis

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestClient
import tadoclient.Application
import tadoclient.models.*
import tadoclient.verify.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - home")
class HomeApi_IT(
    @Qualifier("tadoRestClient")
    val tadoRestClient: RestClient
) {
    val tadoHomeAPI = HomeApi(tadoRestClient)

    var heatingSystemBeforeTest: HeatingSystem? = null

    @BeforeAll
    fun beforeTest() {
        heatingSystemBeforeTest = tadoHomeAPI.getHeatingSystem(HOME_ID)
    }

    @AfterAll
    fun afterTests() {
        // boiler and underfloorHeating
        heatingSystemBeforeTest?.let{
            val boiler = Boiler(
                present = heatingSystemBeforeTest!!.boiler!!.present,
                id = heatingSystemBeforeTest!!.boiler?.id
            )
            tadoHomeAPI.setBoiler(HOME_ID, boiler)
            tadoHomeAPI.setUnderfloorHeating(HOME_ID, UnderfloorHeating(present = heatingSystemBeforeTest!!.underfloorHeating!!.present))
        }
    }

    @Test
    @DisplayName("GET /homes/{homeId}")
    @Order(10)
    fun getHome() {
        val endpoint = "GET /homes/{homeId}"
        val home = assertCorrectResponse { tadoHomeAPI.getHome(HOME_ID) }
        verifyHome(home, endpoint)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/airComfort")
    @Order(20)
    fun getAirComfort() {
        val endpoint = "GET /homes/{homeId}/airComfort"
        val airComfort = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoHomeAPI.getAirComfort(HOME_ID)
        }
        verifyAirComfort(airComfort, endpoint)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/awayRadiusInMeters")
    @Order(25)
    @Disabled("not yet available in spec")
    fun putAwayRadiusInMeters() {
        // TODO: implement once available in spec
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/details")
    @Order(30)
    fun putDetails() {
        val endpoint = "PUT /homes/{homeId}/details"
        val home = tadoHomeAPI.getHome(HOME_ID)
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
        val response = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoHomeAPI.setHomeDetails(HOME_ID, homeDetails)
        }
        assertEquals(Unit, response)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/heatingSystem")
    @Order(50)
    fun getHeatingSystem() {
        val endpoint = "GET /homes/{homeId}/heatingSystem"
        val heatingSystem = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoHomeAPI.getHeatingSystem(HOME_ID)
        }
        verifyHeatingSystem(heatingSystem, endpoint)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/heatingSystem/boiler - not present")
    @Order(60)
    fun putBoiler_NotPresent() {
        val boiler = Boiler(present = false)
        val response = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoHomeAPI.setBoiler(HOME_ID, boiler)
        }
        assertEquals(Unit, response)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/heatingSystem/boiler - present without id")
    @Order(61)
    fun putBoiler_PresentWithoutId() {
        val boiler = Boiler(present = true)
        val response = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoHomeAPI.setBoiler(HOME_ID, boiler)
        }
        assertEquals(Unit, response)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/heatingSystem/boiler - present with id")
    @Order(62)
    fun putBoiler_PresentWithId() {
        val boiler = Boiler(present = true, id = 2699)
        val response = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoHomeAPI.setBoiler(HOME_ID, boiler)
        }
        assertEquals(Unit, response)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/heatingSystem/underfloorHeating")
    @Order(70)
    fun putUnderflootHeating() {
        val underfloorHeating = UnderfloorHeating(present = false)
        val response = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoHomeAPI.setUnderfloorHeating(HOME_ID, underfloorHeating)
        }
        assertEquals(Unit, response)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/incidentDetection")
    @Order(80)
    fun getIncidentDetection() {
        val endpoint = "GET /homes/{homeId}/incidentDetection"
        val incidentDetection = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoHomeAPI.getIncidentDetection(HOME_ID)
        }
        val typeName = "IncidentDetection"
        verifyNested(incidentDetection, endpoint, typeName, typeName)
    }

    @Test
    @DisplayName("PUT /homes/{homeId}/incidentDetection")
    @Order(90)
    fun putIncidentDetection() {
        val endpoint = "PUT /homes/{homeId}/incidentDetection"
        val response = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoHomeAPI.setIncidentDetection(HOME_ID, IncidentDetectionInput(enabled = true))
        }
        assertEquals(Unit, response)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/invitations")
    @Order(95)
    @Disabled("not yet available in spec")
    fun getInvitations() {
        // TODO: implement once available in the spec
    }

    @Test
    @DisplayName("GET /homes/{homeId}/weather")
    @Order(100)
    fun getWeather() {
        val endpoint = "GET /homes/{homeId}/weather"
        val weather = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoHomeAPI.getWeather(HOME_ID)
        }
        verifyWeather(weather, endpoint)
    }

}