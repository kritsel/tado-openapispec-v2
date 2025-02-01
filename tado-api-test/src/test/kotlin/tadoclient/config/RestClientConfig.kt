package tadoclient.config


import com.fasterxml.jackson.databind.DeserializationFeature
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestClient

// https://www.springcloud.io/post/2023-03/springboot-jackson/#gsc.tab=0

@Configuration
open class RestClientConfig(
    val oauthRequestInterceptor: OAuthRequestInterceptor,
    val chaosMonkeyInjectPropertyInterceptor: ChaosMonkeyInjectPropertyInterceptor,
    val chaosMonkeyUseUnknownEnumValueInterceptor: ChaosMonkeyUseUnknownEnumValueInterceptor
) {
    @Bean("tadoRestClient")
    open fun tadoClient(): RestClient {
        return getRestClient(false)
    }

    @Bean("tadoStrictRestClient")
    open fun tadoStrictClient(): RestClient {
        return getRestClient(true)
    }

    @Bean("tadoChaosMonkeyInjectedPropertyClient")
    open fun tadoChaosMonkeyClient2(): RestClient {
        return getRestClient(true)
            .mutate()
            .requestInterceptor(chaosMonkeyInjectPropertyInterceptor)
            .build()
    }

    @DependsOn(value = ["jackson2ObjectMapperBuilder", "tadoJsonCustomizer", "messageConverter"])
    @Bean("tadoChaosMonkeyUnknownEnumValueClient")
    open fun tadoChaosMonkeyClient3(): RestClient {
        return getRestClient(true)
            .mutate()
            .requestInterceptor(chaosMonkeyUseUnknownEnumValueInterceptor)
            .build()
    }

    // this won't take effect for some reason
    @Bean
    open fun tadoJsonCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
        println("init tadoJsonCustomizer")
        return Jackson2ObjectMapperBuilderCustomizer { builder: Jackson2ObjectMapperBuilder ->
            builder
                // neither of these two options work...
                .failOnUnknownProperties(true)
                .featuresToEnable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    @Bean
    @Primary
    open fun jackson2ObjectMapperBuilder(): Jackson2ObjectMapperBuilder {
        println("init jackson2ObjectMapperBuilder")
        return Jackson2ObjectMapperBuilder()
            // neither of these two options work...
            .failOnUnknownProperties(true)
            .featuresToEnable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    @Bean // Convertor method
    open fun messageConverter() : MappingJackson2HttpMessageConverter{
        println("init messageConverter")
        val builder = Jackson2ObjectMapperBuilder()
            .failOnUnknownProperties(true)
            .featuresToEnable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

        val converter = MappingJackson2HttpMessageConverter(builder.build())
        println("converter: $converter")
        return converter
    }

    private fun getRestClient(strict: Boolean): RestClient {
        return RestClient
            .builder()
            .baseUrl("https://my.tado.com/api/v2/")
            .requestInterceptor(oauthRequestInterceptor)
            // because the Jackson2ObjectMapperBuilderCustomizer @Bean solution doesn't work,
            // we'll enable FAIL_ON_UNKNOWN_PROPERTIES here
            .messageConverters { converters ->
//                println("message converters:")
                if (strict) {
                    System.out.println("init strict RestClient")
                    converters.forEach {
//                    println("  " + it.javaClass.simpleName)
                        if (it is MappingJackson2HttpMessageConverter) {
//                            println(
//                                "FAIL_ON_UNKNOWN_PROPERTIES enabled: ${
//                                    it.objectMapper.deserializationConfig.isEnabled(
//                                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
//                                    )
//                                }"
//                            )
//                            println("converter: $it")
//                            println("object mapper: ${it.objectMapper}")
                            it.objectMapper.enable(
                                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
                            )
                        }
                    }
                }
            }
            .build()
    }

}

