package tadoclient.verify

import tadoclient.models.*

fun verifyDeviceExtra(device: DeviceExtra, context:String, parentName:String = "DeviceExtra", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    verifyDevice(device, context, parentName, "DeviceExtra", ancestorObjectProps)
}

fun verifyDevice(device: Device, context:String, parentName:String = "Device", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    verifyDevice(device, context, parentName, "Device", ancestorObjectProps)
}

// TODO: fine tune 'nullAllowedProperties' based on knowledge we have of device types
fun verifyDevice(device: Any, context:String, parentName:String, typeName:String, ancestorObjectProps:Map<String, Any> = emptyMap()) {
    verifyObject(device, context, parentName, typeName, ancestorObjectProps,
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
        emptyCollectionAllowedProperties = listOf("$typeName.characteristics.capabilities"))
}

fun verifyDeviceListItem(deviceListItem: DeviceListItem, context:String, parentName:String, ancestorObjectProps:Map<String, Any> = emptyMap()) {

    val typeName = "DeviceListItem"
    verifyObject(
        deviceListItem, context, parentName, typeName, ancestorObjectProps,
        nullAllowedProperties = listOf(
            "$typeName.zone", // has no value for BR02 and IB01
            "$typeName.zone.duties",// only has value for SU02
            ),
        emptyCollectionAllowedProperties = listOf("$typeName.zone.duties")
    )
}

fun verifyZoneControl(zoneControl: ZoneControl, context:String, parentName:String = "ZoneControl", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    val typeName = "ZoneControl"
    verifyObject(zoneControl, context, parentName, typeName, ancestorObjectProps,
        nullAllowedProperties = listOf(
            "$typeName.duties.driver",
            "$typeName.duties.drivers",
            "$typeName.duties.leader",
            "$typeName.duties.leaders",
            "$typeName.duties.ui",
            "$typeName.duties.uis"))
}

fun verifyInstallation(installation: Installation, context:String, parentName:String = "Installation", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    val typeName = "Installation"
    verifyObject(installation, context, parentName, typeName, ancestorObjectProps,
        nullAllowedProperties = listOf(
            "$typeName.acInstallationInformation.acSpecs.remoteControl.modelName",
            "$typeName.acInstallationInformation.acSpecs.remoteControl.photoS3Key",
            "$typeName.acInstallationInformation.acSettingCommandSetRecording",
            "$typeName.acInstallationInformation.keyCommandSetRecording"
            ))
}
