package testexecutor

import org.apache.maven.shared.invoker.*
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.io.File
import java.io.InputStream


@Component
class AppStartupRunner : ApplicationRunner {

    @Throws(Exception::class)
    override fun run(args: ApplicationArguments) {
        println("AppStartupRunner" )
        val request: InvocationRequest = DefaultInvocationRequest()
        request.setInputStream(InputStream.nullInputStream());
        request.setPomFile(File("${System.getProperty("user.dir")}\\test-harness\\tado-api-test\\pom.xml"))
//        request.addArgs(listOf("clean", "verify", "site"))
        request.addArgs(listOf("site"))

        val outputHandler = InvocationOutputHandler { line -> println(line) }
        request.setOutputHandler(outputHandler)
        request.setErrorHandler(outputHandler)

        val invoker: Invoker = DefaultInvoker()
        invoker.setMavenHome(File(System.getenv("MAVEN_HOME")))

        val result = invoker.execute(request)
        println("maven invoker exit code: ${result.getExitCode()}")
        if (result.executionException != null) {
            println("maven invoker execution exception: ${result.executionException}")
        }
    }
}