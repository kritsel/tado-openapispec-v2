package tadoclient

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableConfigurationProperties(TadoConfig::class)
open class Application

    fun main(args: Array<String>) {
        SpringApplication
            .run(Application::class.java, *args)
    }
