package tadoclient.verify

import tadoclient.models.*

fun verifyDayReport(dayReport:DayReport, context:String, parentName:String = "DayReport", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    val typeName = "DayReport"
//    when (zoneType) {
    when (ancestorObjectProps[ZONE_TYPE]) {

        // TODO: check nullAllowedProperties, also for air_conditioning
        ZoneType.HEATING -> {
            verifyNested(
                dayReport, context, parentName, typeName,
                // settings is only available when a schedule or overlay was active with setting.power = ON
                nullAllowedProperties = listOf(
                    "$typeName.settings",
                    "$typeName.acActivity"),
                ancestorObjectProps = ancestorObjectProps
            )
        }

        // unknown
        ZoneType.AIR_CONDITIONING -> {}

        // DayReport is not supported for zones of ZoneType HOT_WATER
        // so there's nothing to verify
        ZoneType.HOT_WATER -> {}
    }
}




