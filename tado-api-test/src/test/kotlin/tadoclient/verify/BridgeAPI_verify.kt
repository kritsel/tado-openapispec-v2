package tadoclient.verify

import tadoclient.models.*

fun verifyBridge(bridge: Bridge, context:String, fullParentName:String = "User", ancestorObjectProps:Map<String, Any> = emptyMap()){
    val typeName = "Bridge"
    verifyObject(
        bridge, context, fullParentName, typeName, ancestorObjectProps,
        nullAllowedProperties = listOf("$typeName.partner"))
}



