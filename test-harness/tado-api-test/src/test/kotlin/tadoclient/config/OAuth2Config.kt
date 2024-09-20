package tadoclient.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.*
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType


// Based on https://developer.okta.com/blog/2021/05/05/client-credentials-spring-security

@Configuration
open class OAuth2Config {

    /*
    Spring auto-configuration looks for properties with the schema
    spring.security.oauth2.client.registration.[registrationId]
    and creates a ClientRegistration instance within a ClientRegistrationRepository.
    As you will see, in the command line runner version of this, we have to re-create some of this logic manually
    because it is not being auto-configured for us outside of the scope of a web service environment.
     */
    // Create the Okta client registration
    @Bean
    open fun clientRegistration(
        @Value("\${spring.security.oauth2.client.provider.tado.token-uri}") token_uri: String?,
        @Value("\${spring.security.oauth2.client.registration.tado.client-id}") client_id: String?,
        @Value("\${spring.security.oauth2.client.registration.tado.client-secret}") client_secret: String?,
        @Value("\${spring.security.oauth2.client.registration.tado.authorization-grant-type}") authorizationGrantType: String?
    ): ClientRegistration {
        return ClientRegistration
            .withRegistrationId("tado")
            .tokenUri(token_uri)
            .clientId(client_id)
            .clientSecret(client_secret)
            .authorizationGrantType(AuthorizationGrantType(authorizationGrantType))
            .build()
    }

    // Create the client registration repository
    @Bean("myClientRegistrationRepository")
    open fun clientRegistrationRepository(clientRegistration: ClientRegistration?): ClientRegistrationRepository {
        return InMemoryClientRegistrationRepository(clientRegistration)
    }

    // Create the authorized client service
    @Bean
    open fun auth2AuthorizedClientService( @Qualifier("myClientRegistrationRepository") clientRegistrationRepository: ClientRegistrationRepository?): OAuth2AuthorizedClientService {
        return InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository)
    }

    // Create the authorized client manager and service manager using the
    // beans created and configured above
    @Bean
    open fun authorizedClientServiceAndManager(
        clientRegistrationRepository: ClientRegistrationRepository?,
        authorizedClientService: OAuth2AuthorizedClientService?,
        @Value("\${tado.username:undefined}")
        tadoUsername:String,

        @Value("\${tado.password:undefined}")
        tadoPassword:String
    ): AuthorizedClientServiceOAuth2AuthorizedClientManager {
        val authorizedClientProvider =
            OAuth2AuthorizedClientProviderBuilder.builder()
                .password()
                .refreshToken()
                .build()

        val authorizedClientManager =
            AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService
            )
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider)

        authorizedClientManager.setContextAttributesMapper {
            mutableMapOf(
                Pair(OAuth2AuthorizationContext.USERNAME_ATTRIBUTE_NAME, tadoUsername),
                Pair(OAuth2AuthorizationContext.PASSWORD_ATTRIBUTE_NAME, tadoPassword),
            ) as Map<String, Any>?
        }

        return authorizedClientManager
    }
}