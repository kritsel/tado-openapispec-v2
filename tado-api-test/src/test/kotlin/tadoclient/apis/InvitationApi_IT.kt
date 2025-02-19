package tadoclient.apis

import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledIf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestClient
import tadoclient.Application
import tadoclient.TadoConfig
import tadoclient.models.InvitationRequest
import tadoclient.verify.assertCorrectResponse
import tadoclient.verify.verifyObject
import kotlin.test.Test

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - invitation")
class InvitationApi_IT (
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
    val tadoStrictInvitationAPI = InvitationApi(tadoStrictRestClient)

    // We save the token of the invite we are creating, so we can re-use it in the
    // resend and revoke operations
    // This only works because we define the order in which the test methods should be executed
    var createdInvitationToken:String? = null

    @Test
    @DisplayName("POST /homes/{homeId}/invitations")
    @Order(10)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun sendInvitation() {
        val endpoint = "POST /homes/{homeId}/invitations"
        val invitation = assertCorrectResponse { tadoStrictInvitationAPI.sendInvitation(tadoConfig.home!!.id, InvitationRequest("me@example.org")) }
        verifyObject(invitation, endpoint, endpoint, "Invitation")
        this.createdInvitationToken = invitation.token
    }

    @Test
    @DisplayName("POST /homes/{homeId}/invitations/{invitationToken}/resend")
    @Order(20)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun resendInvitation() {
        if (createdInvitationToken != null) {
            val endpoint = "POST /homes/{homeId}/invitations/{invitationToken}/resend"
            val invitation = assertCorrectResponse {
                tadoStrictInvitationAPI.resendInvitation(
                    tadoConfig.home!!.id,
                    createdInvitationToken!!
                )
            }
            verifyObject(invitation, endpoint, endpoint, "Invitation")
        } else {
            fail("no invitation created, so there is no invitation to resend")
        }
    }

    @Test
    @DisplayName("GET /homes/{homeId}/invitations")
    @Order(30)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun getInvitations() {
        if (createdInvitationToken != null) {
            val endpoint = "GET /homes/{homeId}/invitations"
            val invitations = assertCorrectResponse { tadoStrictInvitationAPI.getInvitations(tadoConfig.home!!.id) }
            invitations.forEachIndexed{i, elem -> verifyObject(elem, endpoint, endpoint, "response[$i]")}
        } else {
            fail("no invitation created, so there are no invitations to retrieve")
        }
    }

    @Test
    @DisplayName("DELETE /homes/{homeId}/invitations/{invitationToken}")
    @Order(40)
    @EnabledIf(value = "isHomeConfigured", disabledReason = "no home specified in tado set-up")
    fun revokeInvitation() {
        if (createdInvitationToken != null) {
            tadoStrictInvitationAPI.revokeInvitation(tadoConfig.home!!.id, createdInvitationToken!!)
        } else {
            fail("no invitation created, so there is no invitation to revoke")
        }
    }
}