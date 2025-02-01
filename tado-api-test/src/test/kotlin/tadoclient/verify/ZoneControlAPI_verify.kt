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
            verifyNested(zoneSetting, context, fullParentName, typeName, nullAllowedProperties = nullAllowedProperties, ancestorObjectProps = ancestorObjectProps)
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
            ), ancestorObjectProps = ancestorObjectProps)
        }

        // unknown
        ZoneType.AIR_CONDITIONING -> { }
    }
}

fun verifyZoneOverlay(zoneOverlay: ZoneOverlay, context:String, fullParentName:String = "ZoneOverlay", ancestorObjectProps:Map<String, Any> = emptyMap()){
    val typeName = "ZoneOverlay"
    verifyNested(zoneOverlay, context, fullParentName, typeName,
        // an overlay can be indefinite, meaning that none of time related termination properties will have a value
        nullAllowedProperties = listOf(
            "$typeName.termination.durationInSeconds",
            "$typeName.termination.remainingTimeInSeconds",
            "$typeName.termination.expiry",
            "$typeName.termination.projectedExpiry"),
        ancestorObjectProps = ancestorObjectProps
    )
}

fun verifyZoneAwayConfiguration(zoneAwayConfiguration: ZoneAwayConfiguration, context:String, parentName:String = "ZoneAwayConfiguration", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    val typeName = "ZoneAwayConfiguration"
    verifyNested(zoneAwayConfiguration, context, parentName, typeName,
        nullAllowedProperties = listOf("$typeName.comfortLevel"),
        ancestorObjectProps = ancestorObjectProps
    )
}

fun verifyTimetableType(timetableType: TimetableType, context:String, parentName:String = "TimetableType", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    verifyNested(timetableType, context, parentName, "TimetableType", ancestorObjectProps = ancestorObjectProps)
}

fun verifyTimetableBlock(timetableBlock: TimetableBlock, context:String, parentName:String = "TimetableBlock", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    val typeName = "TimetableBlock"
    verifyNested(
        timetableBlock, context, parentName, typeName,
        ancestorObjectProps = ancestorObjectProps
    )
}

