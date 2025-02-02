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
import tadoclient.models.ZoneType
import tadoclient.verify.ZONE_TYPE
import tadoclient.verify.assertCorrectResponse
import tadoclient.verify.verifyDayReport
import java.time.LocalDate
import java.time.Month
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest(classes = arrayOf( Application::class))
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("tado API - report")
class ReportApi_IT (
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
    private val tadoStrictReportAPI = ReportApi(tadoStrictRestClient)

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/dayReport")
    @Order(1)
    @EnabledIf(value = "isHomeAndHeatingZoneConfigured", disabledReason = "no home and/or heating zone specified in tado set-up")
    fun getDayReport_HEATING() {
        // operation not supported for zones of type HOT_WATER
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/dayReport"
        val dayReport = assertCorrectResponse { tadoStrictReportAPI.getZoneDayReport(tadoConfig.home!!.id, tadoConfig.zone!!.heating!!.id, LocalDate.of(2024, Month.JANUARY, 11)) }
        assertNotNull(dayReport)
        verifyDayReport(dayReport, endpoint, ancestorObjectProps = mapOf(ZONE_TYPE to ZoneType.HEATING))
    }

    @Test
    @DisplayName("GET /homes/{homeId}/zones/{zoneId}/dayReport")
    @Order(2)
    @EnabledIf(value = "isHomeAndAirConZoneConfigured", disabledReason = "no home and/or aircon zone specified in tado set-up")
    fun getDayReport_AIRCON() {
        // operation not supported for zones of type HOT_WATER
        val endpoint = "GET /homes/{homeId}/zones/{zoneId}/dayReport"
        val dayReport = assertCorrectResponse { tadoStrictReportAPI.getZoneDayReport(tadoConfig.home!!.id, tadoConfig.zone!!.airCon!!.id, LocalDate.of(2025, Month.JANUARY, 31)) }
        assertNotNull(dayReport)
        verifyDayReport(dayReport, endpoint, ancestorObjectProps = mapOf(ZONE_TYPE to ZoneType.AIR_CONDITIONING))
    }
}