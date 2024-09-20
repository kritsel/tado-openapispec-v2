package tadoclient.apis

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClient
import tadoclient.Application
import tadoclient.verify.assertNoHttpErrorStatus
import tadoclient.verify.assertUnknownPropertyErrorNotThrown
import tadoclient.verify.verifyUser
import kotlin.test.Test
import kotlin.test.assertNotEquals

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - user")
class UserApi_IT (
    @Qualifier("tadoRestClient")
    val tadoRestClient: RestClient
){
    val tadoUserAPI = UserApi(tadoRestClient)

    @Test
    @DisplayName("GET /me")
    @Order(10)
    fun getMe() {
        val endpoint = "GET /me"
        val user = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoUserAPI.getMe()
        }
        verifyUser(user, endpoint)
    }

    @Test
    @DisplayName("GET /homes/{homeId}/users")
    @Order(20)
    fun getUsers() {
        val endpoint = "GET /homes/{homeId}/users"
        val users = assertUnknownPropertyErrorNotThrown {
            assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
                tadoUserAPI.getUsers(HOME_ID)
            }
        }

        // check users
        assertNotEquals(0, users.size)
        users.forEachIndexed{i, elem -> verifyUser(elem, endpoint, "response[$i]")}
    }
}