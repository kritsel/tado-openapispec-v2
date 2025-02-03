package tadoclient.apis

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.condition.EnabledIf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestClient
import tadoclient.Application
import tadoclient.TadoConfig
import tadoclient.verify.assertCorrectResponse
import tadoclient.verify.verifyUser
import kotlin.test.Test
import kotlin.test.assertNotEquals

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - user")
class UserApi_IT (
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
    val tadoStrictUserAPI = UserApi(tadoStrictRestClient)

    @Test
    @DisplayName("GET /me")
    @Order(10)
    fun getMe() {
        val endpoint = "GET /me"
        val user = assertCorrectResponse { tadoStrictUserAPI.getMe() }
        verifyUser(user, endpoint)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/users")
    @Order(20)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getUsers() {
        val endpoint = "GET /homes/{homeId}/users"
        val users = assertCorrectResponse { tadoStrictUserAPI.getUsers(tadoConfig.home!!.id) }

        // check users
        assertNotEquals(0, users.size)
        users.forEachIndexed{i, elem -> verifyUser(elem, endpoint, "response[$i]")}
    }
}