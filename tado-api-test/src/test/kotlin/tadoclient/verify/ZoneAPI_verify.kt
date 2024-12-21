package tadoclient.verify

import tadoclient.models.*
import kotlin.test.assertNotEquals

fun verifyZone(zone: Zone, context:String, fullParentName:String = "Zone") {
    val typeName = "Zone"
    verifyNested(zone, context, fullParentName, typeName,
        stopAtProperties = listOf("$typeName.devices"))

    // devices
    assertNotEquals(0, zone.devices!!.size)
    zone.devices?.forEachIndexed() {i, elem -> verifyDeviceExtra(elem, context, "$fullParentName.devices[$i]")}
}

fun verifyZoneCapabilities(zoneInfo: Pair<ZoneType, Boolean>, zoneCapabilities: ZoneCapabilities, context:String, parentName:String = "ZoneCapabilities") {
    val typeName="ZoneCapabilities"
    when(zoneInfo.first) {
        ZoneType.HEATING -> {
            verifyNested(zoneCapabilities, context, parentName, parentName, nullAllowedProperties = listOf("$typeName.canSetTemperature", "$typeName.AUTO", "$typeName.COOL", "$typeName.HEAT", "$typeName.DRY", "$typeName.FAN", "$typeName.initialStates"))
        }
        ZoneType.AIR_CONDITIONING -> {
            verifyNested(zoneCapabilities, context, parentName, parentName, nullAllowedProperties = listOf("$typeName.canSetTemperature", "$typeName.temperatures"))
        }
        ZoneType.HOT_WATER -> {
            verifyNested(zoneCapabilities, context, parentName, parentName, nullAllowedProperties = listOf("$typeName.temperatures", "$typeName.AUTO", "$typeName.COOL", "$typeName.HEAT", "$typeName.DRY", "$typeName.FAN", "$typeName.initialStates"))
        }
    }
}

fun verifyZoneState(zoneInfo: Pair<ZoneType, Boolean>, zoneState: ZoneState, context:String, fullParentName:String = "ZoneState"){
    val typeName = "ZoneState"

    // properties which can be null for any zoneType
    val basicNullAllowedProperties = listOf(
        "$typeName.geolocationOverrideDisableTime",
        "$typeName.preparation",
        "$typeName.overlay",
        "$typeName.overlayType",
        "$typeName.nextScheduleChange",
        "$typeName.openWindow",
        "$typeName.link.reason")

    // properties which should not be inspected by verifyNested for any zoneType
    val basicStopAtProperties = listOf(
        "$typeName.overlay",
        "$typeName.setting",
        "$typeName.nextScheduleChange.setting")

    when (zoneInfo.first) {

        ZoneType.HEATING -> {
            verifyNested(zoneState, context, fullParentName, typeName,
                nullAllowedProperties = basicNullAllowedProperties,
                stopAtProperties = basicStopAtProperties)
        }

        ZoneType.HOT_WATER -> {
            val hotWaterNullAllowedProperties = mutableListOf(
                "$typeName.nextTimeBlock",
                "$typeName.activityDataPoints.heatingPower",
                "$typeName.sensorDataPoints.humidity",
                "$typeName.sensorDataPoints.insideTemperature")
            hotWaterNullAllowedProperties.addAll(basicNullAllowedProperties)
            verifyNested(zoneState, context, fullParentName, typeName,
                nullAllowedProperties = hotWaterNullAllowedProperties,
                stopAtProperties = basicStopAtProperties)
        }

        // unknonw
        ZoneType.AIR_CONDITIONING -> {}
    }

    zoneState.overlay?.let {
        verifyZoneOverlay(zoneInfo, it, context, "$fullParentName.overlay")
    }

    zoneState.setting?.let {
        verifyZoneSetting(zoneInfo, it, context, "$fullParentName.setting")
    }

    zoneState.nextScheduleChange?.setting?.let {
        verifyZoneSetting(zoneInfo, it, context, "$fullParentName.nextScheduleChange.setting")
    }
}





