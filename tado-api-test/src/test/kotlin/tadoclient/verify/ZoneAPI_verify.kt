package tadoclient.verify

import tadoclient.models.*
import kotlin.test.assertNotEquals

const val ZONE_TYPE = "zone.type"

fun verifyZone(zone: Zone, context:String, fullParentName:String = "Zone", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    val typeName = "Zone"
    verifyObject(zone, context, fullParentName, typeName, ancestorObjectProps)

    // devices
    assertNotEquals(0, zone.devices!!.size)
}

fun verifyZoneCapabilities(zoneCapabilities: ZoneCapabilities, context:String, parentName:String = "ZoneCapabilities", ancestorObjectProps:Map<String, Any> = emptyMap() ) {
    val typeName="ZoneCapabilities"
    when(ancestorObjectProps[ZONE_TYPE]) {
        ZoneType.HOT_WATER -> {
            verifyObject(
                zoneCapabilities, context, parentName, parentName, ancestorObjectProps,
                nullAllowedProperties = listOf(
                    "$typeName.temperatures",
                    "$typeName.AUTO",
                    "$typeName.COOL",
                    "$typeName.HEAT",
                    "$typeName.DRY",
                    "$typeName.FAN",
                    "$typeName.initialStates"))
        }

        ZoneType.HEATING -> {
            verifyObject(
                zoneCapabilities, context, parentName, parentName, ancestorObjectProps,
                nullAllowedProperties = listOf(
                    "$typeName.canSetTemperature",
                    "$typeName.AUTO",
                    "$typeName.COOL",
                    "$typeName.HEAT",
                    "$typeName.DRY",
                    "$typeName.FAN",
                    "$typeName.initialStates"))
        }

        ZoneType.AIR_CONDITIONING -> {
            verifyObject(
                zoneCapabilities, context, parentName, parentName, ancestorObjectProps,
                nullAllowedProperties = listOf(
                    "$typeName.canSetTemperature",
                    "$typeName.temperatures"))
        }
    }
}

fun verifyZoneState(zoneState: ZoneState, context:String, fullParentName:String = "ZoneState", ancestorObjectProps:Map<String, Any> = emptyMap() ){
    val typeName = "ZoneState"

    // properties which can be null for any zoneType
    val basicNullAllowedProperties = listOf(
        "$typeName.geolocationOverrideDisableTime",
        "$typeName.preparation",
        "$typeName.overlay",
        "$typeName.overlayType",
        "$typeName.nextScheduleChange",
        "$typeName.nextTimeBlock",
        "$typeName.openWindow",
        "$typeName.link.reason")

    when (ancestorObjectProps[ZONE_TYPE]) {

        ZoneType.HOT_WATER -> {
            val hotWaterNullAllowedProperties = mutableListOf(
                "$typeName.activityDataPoints.heatingPower",    // specific for HEATING
                "$typeName.activityDataPoints.acPower",         // specific for AIR_CONDITIONING
                "$typeName.sensorDataPoints.humidity",          // specific for HEATING or AIR_CONDITIONING
                "$typeName.sensorDataPoints.insideTemperature") // specific for HEATING or AIR_CONDITIONING
            hotWaterNullAllowedProperties.addAll(basicNullAllowedProperties)
            verifyObject(zoneState, context, fullParentName, typeName, ancestorObjectProps,
                nullAllowedProperties = hotWaterNullAllowedProperties)
        }

        ZoneType.HEATING -> {
            val heatingNullAllowedProperties = mutableListOf(
                "$typeName.activityDataPoints.acPower") // specific for AIR_CONDITIONING
            heatingNullAllowedProperties.addAll(basicNullAllowedProperties)
            verifyObject(zoneState, context, fullParentName, typeName, ancestorObjectProps,
                nullAllowedProperties = heatingNullAllowedProperties)
        }

        // unknown
        ZoneType.AIR_CONDITIONING -> {
            val airConNullAllowedProperties = mutableListOf(
                "$typeName.activityDataPoints.heatingPower") // specific for HEATING
            airConNullAllowedProperties.addAll(basicNullAllowedProperties)
            verifyObject(zoneState, context, fullParentName, typeName, ancestorObjectProps,
                nullAllowedProperties = airConNullAllowedProperties
            )
        }
    }
}





