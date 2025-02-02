package tadoclient.verify

import tadoclient.models.*

fun verifyZoneSetting(zoneSetting: ZoneSetting, context:String, fullParentName:String = "ZoneSetting", ancestorObjectProps:Map<String, Any> = emptyMap()){
    val typeName = "ZoneSetting"
    when (ancestorObjectProps[ZONE_TYPE]) {

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
            verifyObject(zoneSetting, context, fullParentName, typeName, ancestorObjectProps, nullAllowedProperties = nullAllowedProperties)
        }

        // only type and power are expected to have a value, all other properties are allowed to be null
        ZoneType.HOT_WATER -> {
            verifyObject(zoneSetting, context, fullParentName, typeName, ancestorObjectProps, nullAllowedProperties = mutableListOf(
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

fun verifyZoneOverlay(zoneOverlay: ZoneOverlay, context:String, fullParentName:String = "ZoneOverlay", ancestorObjectProps:Map<String, Any> = emptyMap()){
    val typeName = "ZoneOverlay"
    verifyObject(zoneOverlay, context, fullParentName, typeName, ancestorObjectProps,
        // an overlay can be indefinite, meaning that none of time related termination properties will have a value
        nullAllowedProperties = listOf(
            "$typeName.termination.durationInSeconds",
            "$typeName.termination.remainingTimeInSeconds",
            "$typeName.termination.expiry",
            "$typeName.termination.projectedExpiry"))
}

fun verifyZoneAwayConfiguration(zoneAwayConfiguration: ZoneAwayConfiguration, context:String, parentName:String = "ZoneAwayConfiguration", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    val typeName = "ZoneAwayConfiguration"
    verifyObject(zoneAwayConfiguration, context, parentName, typeName, ancestorObjectProps,
        nullAllowedProperties = listOf("$typeName.comfortLevel"))
}

fun verifyTimetableType(timetableType: TimetableType, context:String, parentName:String = "TimetableType", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    verifyObject(timetableType, context, parentName, "TimetableType", ancestorObjectProps)
}

fun verifyTimetableBlock(timetableBlock: TimetableBlock, context:String, parentName:String = "TimetableBlock", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    val typeName = "TimetableBlock"
    verifyObject(
        timetableBlock, context, parentName, typeName, ancestorObjectProps)
}

