package tadoclient.verify

import tadoclient.models.*

fun verifyDayReport(zoneType: ZoneType, dayReport:DayReport, context:String, parentName:String = "DayReport") {
    val typeName = "DayReport"
    when (zoneType) {

        ZoneType.HEATING -> {
            verifyNested(
                dayReport, context, parentName, typeName,
                // settings is only available when a schedule or overlay was active with setting.power = ON
                nullAllowedProperties = listOf("$typeName.settings"),
                stopAtProperties = listOf("$typeName.stripes.dataIntervals[i].value.setting")
            )

            dayReport.stripes?.dataIntervals?.forEachIndexed { i, elem ->
                elem.value?.setting?.let {
                    verifyZoneSetting(
                        Pair(zoneType, true),
                        it,
                        context,
                        "$parentName.stripes.dataIntervals[$i].value.setting"
                    )
                }
            }
        }

        // unknown
        ZoneType.AIR_CONDITIONING -> {}

        // DayReport is not supported for zones of ZoneType HOT_WATER
        // so there's nothing to verify
        ZoneType.HOT_WATER -> {}
    }
}




