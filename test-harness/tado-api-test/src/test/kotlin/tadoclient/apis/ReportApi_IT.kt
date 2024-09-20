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
import tadoclient.models.ZoneType
import tadoclient.verify.assertNoHttpErrorStatus
import tadoclient.verify.verifyDayReport
import java.time.LocalDate
import java.time.Month
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - report")
class ReportApi_IT (
    @Qualifier("tadoRestClient")
    val tadoRestClient: RestClient
){
    private val tadoReportAPI = ReportApi(tadoRestClient)

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/dayReport")
    @Order(1)
    fun getDayReport() {
        // operation not supported for zones of type HOT_WATER
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/dayReport"
        val dayReport = assertNoHttpErrorStatus(HttpStatus.FORBIDDEN) {
            tadoReportAPI.getZoneDayReport(HOME_ID, ZONE_ID, LocalDate.of(2024, Month.JANUARY, 11))
        }
        assertNotNull(dayReport)
        verifyDayReport(ZoneType.HEATING, dayReport, endpoint)
    }
}