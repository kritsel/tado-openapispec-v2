package tadoclient.apis


import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestClient
import tadoclient.Application
import kotlin.test.Test
import kotlin.test.assertTrue

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("Chaos monkey tests")
class ChaosMonkey_IT (

    // this RestClient inserts a property named 'chaos' to the response JSON before it gets deserialized
    @Qualifier("tadoChaosMonkeyInjectedPropertyClient")
    val tadoChaosMonkeyInjectedPropertyRestClient: RestClient,

    // this RestClient returns the default content '{"presence": "CHAOS"}'
    @Qualifier("tadoChaosMonkeyUnknownEnumValueClient")
    val tadoChaosMonkeyUnknownEnumValueRestClient: RestClient,

    @Value("\${tado.home.id:-1}")
    val homeId: Long,
){
    val chaosMonkeyInjectedPropertyClient = HomeControlApi(tadoChaosMonkeyInjectedPropertyRestClient)

    val chaosMonkeyUnknownEnumValueClient = HomeControlApi(tadoChaosMonkeyUnknownEnumValueRestClient)

    @Test
    @DisplayName("insert unknown property into response, must trigger an exception")
    @Order(10)
    fun testUnknownProperty() {
        var unknownPropertyExceptionCaught = false
        try {
            val result = chaosMonkeyInjectedPropertyClient.getHomeState(homeId)
            println(result)
        } catch(e:Exception) {
            println("exception")
            println(e)
            if (e.cause != null && e.cause is HttpMessageNotReadableException) {
                if (e.cause!!.cause != null && e.cause!!.cause is UnrecognizedPropertyException) {
                    unknownPropertyExceptionCaught = true
                }
            }
        }
        assertTrue(unknownPropertyExceptionCaught, "expected UnrecognizedPropertyException")
    }

    @Test
    @DisplayName("replace valid enum value with unknown value in response, must trigger an exception")
    @Order(20)
    fun testInvalidEnumValue() {
        var expectedExceptionCaught = false
        try {
            val result = chaosMonkeyUnknownEnumValueClient.getHomeState(homeId)
        } catch(e:Exception) {
            if (e.cause != null && e.cause is HttpMessageNotReadableException) {
                if (e.cause!!.cause != null && e.cause!!.cause is InvalidFormatException) {
                    expectedExceptionCaught = true
                }
            }
        }
        assertTrue(expectedExceptionCaught, "expected InvalidFormatException")
    }
}