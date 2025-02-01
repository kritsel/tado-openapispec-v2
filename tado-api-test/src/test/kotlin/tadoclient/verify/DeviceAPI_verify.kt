package tadoclient.verify

import tadoclient.models.Device
import tadoclient.models.DeviceExtra
import tadoclient.models.DeviceListItem

fun verifyDeviceExtra(device: DeviceExtra, context:String, parentName:String = "DeviceExtra", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    verifyDevice(device, context, parentName, "DeviceExtra", ancestorObjectProps)
}

fun verifyDevice(device: Device, context:String, parentName:String = "Device", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    verifyDevice(device, context, parentName, "Device", ancestorObjectProps)
}

// TODO: fine tune 'nullAllowedProperties' based on knowledge we have of device types
fun verifyDevice(device: Any, context:String, parentName:String, typeName:String, ancestorObjectProps:Map<String, Any> = emptyMap()) {
    verifyNested(device, context, parentName, typeName,
        nullAllowedProperties = listOf(
            "$typeName.mountingState",
            "$typeName.mountingStateWithError",
            "$typeName.batteryState",
            "$typeName.orientation",
            "$typeName.childLockEnabled",
            "$typeName.isDriverConfigured",
            "$typeName.inPairingMode",
            "$typeName.characteristics",  // not available for IB01
            "$typeName.accessPointWiFi",  // only available for WR02
            "$typeName.commandTableUploadState"  // only available for WR02
        ),
        // capabilities empty for BR02
        emptyCollectionAllowedProperties = listOf("$typeName.characteristics.capabilities"),
        ancestorObjectProps = ancestorObjectProps)
}

fun verifyDeviceListItem(deviceListItem: DeviceListItem, context:String, parentName:String, ancestorObjectProps:Map<String, Any> = emptyMap()) {

    val typeName = "DeviceListItem"
    verifyNested(
        deviceListItem, context, parentName, typeName,
        nullAllowedProperties = listOf(
            "$typeName.zone", // has no value for BR02 and IB01
            "$typeName.zone.duties",// only has value for SU02
            ),
        emptyCollectionAllowedProperties = listOf("$typeName.zone.duties"),
        ancestorObjectProps = ancestorObjectProps
    )
}