package tadoclient.verify

import tadoclient.models.*

fun verifyDayReport(dayReport:DayReport, context:String, parentName:String = "DayReport", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    val typeName = "DayReport"
//    when (zoneType) {
    when (ancestorObjectProps[ZONE_TYPE]) {

        // TODO: check nullAllowedProperties, also for air_conditioning
        ZoneType.HEATING -> {
            verifyObject(
                dayReport, context, parentName, typeName, ancestorObjectProps,
                // settings is only available when a schedule or overlay was active with setting.power = ON
                nullAllowedProperties = listOf(
                    "$typeName.settings",
                    "$typeName.acActivity"))
        }

        // unknown
        ZoneType.AIR_CONDITIONING -> {
            verifyObject(
                dayReport, context, parentName, typeName, ancestorObjectProps,
                // settings is only available when a schedule or overlay was active with setting.power = ON
                nullAllowedProperties = listOf(
                    "$typeName.settings",
                    "$typeName.callForHeat",
                    "$typeName.hotWaterProduction"
                ))
        }

        // DayReport is not supported for zones of ZoneType HOT_WATER
        // so there's nothing to verify
        ZoneType.HOT_WATER -> {}
    }
}

fun verifyStripesDataInterval(stripesDataInterval: StripesDataInterval, context:String, parentName:String = "DayReport", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    val typeName = "StripesDataInterval"
    verifyObject(
        stripesDataInterval, context, parentName, typeName, ancestorObjectProps,
        // settings is only available when a schedule or overlay was active with setting.power = ON
        nullAllowedProperties = listOf(
            "$typeName.value.setting"))
}

fun verifyWeatherConditionDataInterval(weatherConditionDataInterval: WeatherConditionDataInterval, context:String, parentName:String = "DayReport", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    val typeName = "WeatherConditionDataInterval"
    verifyObject(
        weatherConditionDataInterval, context, parentName, typeName, ancestorObjectProps,
        // in one of the test runs the 'value' property appeared to be null
        nullAllowedProperties = listOf(
            "$typeName.value"))
}

fun verifyWeatherSlots(weatherSlotTimeSeriesSlots: WeatherSlotTimeSeriesSlots, context:String, parentName:String = "DayReport", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    val typeName = "WeatherSlotTimeSeriesSlots"
    verifyObject(
        weatherSlotTimeSeriesSlots, context, parentName, typeName, ancestorObjectProps,
        // when requesting the dayreport for a date more than a year ago, these fields can be null
        nullAllowedProperties = listOf(
            "$typeName.04colon00",
            "$typeName.08colon00",
            "$typeName.12colon00",
            "$typeName.16colon00",
            "$typeName.20colon00"))
}




