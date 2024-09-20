package tadoclient.verify

import tadoclient.models.Device
import tadoclient.models.DeviceExtra
import tadoclient.models.DeviceListItem

fun verifyDeviceExtra(device: DeviceExtra, context:String, parentName:String = "DeviceExtra") {
    verifyDevice(device, context, parentName, "DeviceExtra")
}

fun verifyDevice(device: Device, context:String, parentName:String = "Device") {
    verifyDevice(device, context, parentName, "Device")
}

// TODO: fine tune 'nullAllowedProperties' based on knowledge we have of device types
fun verifyDevice(device: Any, context:String, parentName:String, typeName:String) {
    verifyNested(device, context, parentName, typeName,
        nullAllowedProperties = listOf(
            "$typeName.mountingState",
            "$typeName.mountingStateWithError",
            "$typeName.batteryState",
            "$typeName.orientation",
            "$typeName.childLockEnabled",
            "$typeName.isDriverConfigured",
            "$typeName.inPairingMode",
            "$typeName.characteristics"  // not available for IB01
        ),
        // capabilities empty for BR02
        emptyCollectionAllowedProperties = listOf("$typeName.characteristics.capabilities"))
}

fun verifyDeviceListItem(deviceListItem: DeviceListItem, context:String, parentName:String) {

    val typeName = "DeviceListItem"
    verifyNested(
        deviceListItem, context, parentName, typeName,
        nullAllowedProperties = listOf(
            "$typeName.zone", // has no value for BR02 and IB01
            "$typeName.zone.duties",// only has value for SU02
            ),

        stopAtProperties = listOf("$typeName.device"),
        emptyCollectionAllowedProperties = listOf("$typeName.zone.duties")
    )

    // verify device in entries
    verifyDevice(deviceListItem.device!!, context, "$typeName.device")
}