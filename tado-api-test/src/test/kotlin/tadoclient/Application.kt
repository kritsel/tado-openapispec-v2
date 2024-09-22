package tadoclient

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties

@SpringBootApplication
@EnableConfigurationProperties(TadoConfig::class)
open class Application

    fun main(args: Array<String>) {
        SpringApplication
            .run(Application::class.java, *args)
    }
