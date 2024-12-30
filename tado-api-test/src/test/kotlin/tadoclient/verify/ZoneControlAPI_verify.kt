package tadoclient.verify

import tadoclient.models.*

fun verifyZoneSetting(zoneInfo: Pair<ZoneType, Boolean>, zoneSetting: ZoneSetting, context:String, fullParentName:String = "ZoneSetting"){
    val typeName = "ZoneSetting"
    when (zoneInfo.first) {

        ZoneType.HEATING -> {
            // start with the properties which are only applicable to an AIR_CONDITIONING zone
            var nullAllowedProperties = mutableListOf(
                "$typeName.fanLevel",
                "$typeName.verticalSwing",
                "$typeName.horizontalSwing",
                "$typeName.light",
                "$typeName.mode",
                "$typeName.isBoost"
            )
            // when the zone is OFF, there will not be a temperature value either
            if (zoneSetting.power == Power.OFF) {
                nullAllowedProperties.add("$typeName.temperature")
            }
            verifyNested(zoneSetting, context, fullParentName, typeName, nullAllowedProperties = nullAllowedProperties)
        }

        // only type and power are expected to have a value, all other properties are allowed to be null
        ZoneType.HOT_WATER -> {
            verifyNested(zoneSetting, context, fullParentName, typeName, nullAllowedProperties = mutableListOf(
                "$typeName.fanLevel",
                "$typeName.verticalSwing",
                "$typeName.horizontalSwing",
                "$typeName.light",
                "$typeName.mode",
                "$typeName.temperature",
                "$typeName.isBoost"
            ))
        }

        // unknown
        ZoneType.AIR_CONDITIONING -> { }
    }
}

fun verifyZoneOverlay(zoneInfo: Pair<ZoneType, Boolean>, zoneOverlay: ZoneOverlay, context:String, fullParentName:String = "ZoneOverlay"){
    val typeName = "ZoneOverlay"
    verifyNested(zoneOverlay, context, fullParentName, typeName,
        // an overlay can be indefinite, meaning that none of time related terminiation properties will have a value
        nullAllowedProperties = listOf(
            "$typeName.termination.durationInSeconds",
            "$typeName.termination.remainingTimeInSeconds",
            "$typeName.termination.expiry",
            "$typeName.termination.projectedExpiry"),
        stopAtProperties = listOf("$typeName.setting"))

    // verify the ZoneSetting of this overlay
    verifyZoneSetting(zoneInfo, zoneOverlay.setting!!, context, "$fullParentName.setting")
}

fun verifyZoneAwayConfiguration(zoneInfo: Pair<ZoneType, Boolean>, zoneAwayConfiguration: ZoneAwayConfiguration, context:String, parentName:String = "ZoneAwayConfiguration") {
    val typeName = "ZoneAwayConfiguration"
    verifyNested(zoneAwayConfiguration, context, parentName, typeName,
        nullAllowedProperties = listOf("$typeName.comfortLevel"),
        stopAtProperties = listOf("$typeName.setting"))

    // verify setting
    verifyZoneSetting(zoneInfo, zoneAwayConfiguration.setting!!, context, "$parentName.setting")
}

fun verifyTimetableType(timetableType: TimetableType, context:String, parentName:String = "TimetableType") {
    verifyNested(timetableType, context, parentName, "TimetableType")
}

fun verifyTimetableBlock(zoneInfo: Pair<ZoneType, Boolean>, timetableBlock: TimetableBlock, context:String, parentName:String = "TimetableBlock") {
    val typeName = "TimetableBlock"
    verifyNested(timetableBlock, context, parentName, typeName, stopAtProperties = listOf("$typeName.setting"))

    // verify setting
    verifyZoneSetting(zoneInfo, timetableBlock.setting!!, context, "$parentName.setting")
}

