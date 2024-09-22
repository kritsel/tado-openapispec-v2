package testexecutor

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
open class Application

    fun main(args: Array<String>) {
        SpringApplication
            .run(Application::class.java, *args)
            .close()
    }
