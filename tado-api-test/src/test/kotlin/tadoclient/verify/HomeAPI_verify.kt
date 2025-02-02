package tadoclient.verify

import tadoclient.models.*

fun verifyHome(home:Home, context:String, parentName:String = "Home", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    val typeName = "Home"
    verifyObject(home, context, parentName, typeName, ancestorObjectProps,
        nullAllowedProperties = listOf(
            "$typeName.isBalanceHpEligible",
            "$typeName.partner",
            "$typeName.contactDetails.name",
            "$typeName.contactDetails.email",
            "$typeName.contactDetails.phone",
            "$typeName.address.addressLine1",
            "$typeName.address.addressLine2",
            "$typeName.address.city",
            "$typeName.address.state",
            "$typeName.address.country"),
        emptyCollectionAllowedProperties = listOf("$typeName.skills"))
}

fun verifyAirComfort(airComfort: AirComfort, context:String, parentName:String = "AirComfort", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    val typeName = "AirComfort"
    verifyObject(airComfort, context, parentName, typeName, ancestorObjectProps,
        //  Properties below are not present when there is (temporarily) no connection with the measuring device in the room
        //  (e.g because of an empty device battery).
        nullAllowedProperties = listOf(
            "$typeName.comfort[i].temperatureLevel",
            "$typeName.comfort[i].humidityLevel",
            "$typeName.comfort[i].coordinate"))
}

fun verifyHeatingSystem(heatingSystem: HeatingSystem, context:String, parentName:String = "HeatingSystem", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    val typeName = "HeatingSystem"
    verifyObject(heatingSystem, context, parentName, typeName, ancestorObjectProps,
        nullAllowedProperties = listOf(
            "$typeName.boiler.id",
            "$typeName.boiler.found"))
}

fun verifyHeatingCircuit(heatingCircuit: HeatingCircuit, context:String, parentName:String = "HeatingCircuit", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    val typeName = "HeatingSystem"
    verifyObject(heatingCircuit, context, parentName, typeName, ancestorObjectProps)
}

fun verifyWeather(weather: Weather, context:String, parentName:String = "Weather", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    val typeName = "Weather"
    verifyObject(weather, context, parentName, typeName, ancestorObjectProps)
}



