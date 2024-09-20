package tadoclient.verify

import tadoclient.models.MobileDevice

fun verifyMobileDevice(mobileDevice: MobileDevice, context:String, fullParentName:String = "MobileDevice"){
    val typeName = "MobileDevice"
    verifyNested(mobileDevice, context, fullParentName, typeName,
        nullAllowedProperties = listOf(
            // location only available for devices which use geofencing
            "$typeName.location",
            // push notifications only seem to be available for phones, not for tables/iPads
            "$typeName.settings.pushNotifications"))
}
