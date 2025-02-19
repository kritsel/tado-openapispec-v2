package tadoclient.verify

import tadoclient.models.*
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaMethod
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

fun verifyObjectDispatch(anObject:Any, context:String, fullParentName:String, parentName:String, ancestorObjectProps:Map<String, Any>, nullAllowedProperties:List<String> = emptyList(), emptyCollectionAllowedProperties:List<String> = emptyList()) {
    // Device
    if (anObject is Device) {
        verifyDevice(anObject, context, fullParentName, ancestorObjectProps)
    } else if (anObject is DeviceExtra) {
        verifyDeviceExtra(anObject, context, fullParentName, ancestorObjectProps)
    } else if (anObject is ZoneControl) {
        verifyZoneControl(anObject, context, fullParentName, ancestorObjectProps)

    // Home
    } else if (anObject is Home) {
        verifyHome(anObject, context, fullParentName, ancestorObjectProps)
    } else if (anObject is AirComfort) {
        verifyAirComfort(anObject, context, fullParentName, ancestorObjectProps)
    } else if (anObject is HeatingSystem) {
        verifyHeatingSystem(anObject, context, fullParentName, ancestorObjectProps)
    } else if (anObject is HeatingCircuit) {
        verifyHeatingCircuit(anObject, context, fullParentName, ancestorObjectProps)
    } else if (anObject is Weather) {
        verifyWeather(anObject, context, fullParentName, ancestorObjectProps)

    // Home control
    } else if (anObject is HomeState) {
        verifyHomeState(anObject, context, fullParentName, ancestorObjectProps)

    // MobileDevice
    } else if (anObject is MobileDevice) {
        verifyMobileDevice(anObject, context, fullParentName, ancestorObjectProps)

    // User
    } else if (anObject is User) {
        verifyUser(anObject, context, fullParentName, ancestorObjectProps)

    // Zone
    } else if (anObject is Zone) {
        verifyZone(anObject, context, fullParentName, ancestorObjectProps)
    } else if (anObject is ZoneCapabilities) {
        verifyZoneCapabilities(anObject, context, fullParentName, ancestorObjectProps)
    } else if (anObject is ZoneState) {
        verifyZoneState(anObject, context, fullParentName, ancestorObjectProps)

    // Zone control
    } else if (anObject is ZoneSetting) {
        verifyZoneSetting(anObject, context, fullParentName, ancestorObjectProps)
    } else if (anObject is ZoneOverlay) {
        verifyZoneOverlay(anObject, context, fullParentName, ancestorObjectProps)
    } else if (anObject is ZoneAwayConfiguration) {
        verifyZoneAwayConfiguration(anObject, context, fullParentName, ancestorObjectProps)
    } else if (anObject is TimetableType) {
        verifyTimetableType(anObject, context, fullParentName, ancestorObjectProps)
    } else if (anObject is TimetableBlock) {
        verifyTimetableBlock(anObject, context, fullParentName, ancestorObjectProps)

    // Report
    } else if (anObject is StripesDataInterval) {
        verifyStripesDataInterval(anObject, context, fullParentName, ancestorObjectProps)
    } else if (anObject is WeatherConditionDataInterval) {
        verifyWeatherConditionDataInterval(anObject, context, fullParentName, ancestorObjectProps)
    } else if (anObject is WeatherSlotTimeSeriesSlots) {
        verifyWeatherSlots(anObject, context, fullParentName, ancestorObjectProps)

    // generic
    } else {
        verifyObject(anObject, context, fullParentName, parentName, ancestorObjectProps, nullAllowedProperties, emptyCollectionAllowedProperties)
    }
}

// when a null-value property is found which should have a value
// or an empty collection which should contain elements, this results in something like
// org.opentest4j.AssertionFailedError: property Home.partner is null ==> expected: not <null>
fun verifyObject(anObject:Any, context:String, fullParentName:String, parentName:String, ancestorObjectProps:Map<String, Any> = emptyMap(), nullAllowedProperties:List<String> = emptyList(), emptyCollectionAllowedProperties:List<String> = emptyList()) {
    anObject::class.memberProperties.forEach { property ->
        val fullFQName = "$fullParentName.${property.name}"
        val fqName = "$parentName.${property.name}"
        if (fqName !in nullAllowedProperties) {
            assertNotNull(
                (property as KProperty1<Any, *>).get(anObject),
                "[$context] property $fullFQName is null"
            )
        }

        property.isAccessible = true
        val value = property.getter.javaMethod!!.invoke(anObject)
        value?.let {
            if (value.javaClass.simpleName in simpleTypes) {
                // nothing more to do
            } else if (value is List<*>) {
                if (fqName !in emptyCollectionAllowedProperties) {
                    assertNotEquals(0, value.size, "$fullFQName is an empty list ($fqName)")
                }
                value.forEachIndexed {i, elem -> verifyObjectDispatch(elem!!, context, "$fullFQName[$i]", "$fqName[i]", ancestorObjectProps, nullAllowedProperties, emptyCollectionAllowedProperties, ) }
            } else if (value is Map<*, *>) {
                value.forEach { key, value ->  verifyObjectDispatch(value!!, context, "$fullFQName[$key]", "$fqName[*]", ancestorObjectProps, nullAllowedProperties, emptyCollectionAllowedProperties) }
            } else {
                verifyObjectDispatch(value, context, fullFQName, fqName, ancestorObjectProps, nullAllowedProperties, emptyCollectionAllowedProperties)
            }
        }

    }
}

val simpleTypes:List<String> = listOf(
    String::class.javaObjectType.simpleName,
    Boolean::class.javaObjectType.simpleName,
    Integer::class.javaObjectType.simpleName,
    Long::class.javaObjectType.simpleName,
    Double::class.javaObjectType.simpleName,
    Float::class.javaObjectType.simpleName,
    Boolean::class.javaObjectType.simpleName,
    Instant::class.javaObjectType.simpleName,
    LocalDate::class.javaObjectType.simpleName,
    LocalTime::class.javaObjectType.simpleName,
    LocalDateTime::class.javaObjectType.simpleName,
    UUID::class.javaObjectType.simpleName)
