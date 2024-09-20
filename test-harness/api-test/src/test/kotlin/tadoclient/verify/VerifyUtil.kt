package tadoclient.verify

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

// when a null-value property is found which should have a value
// or an empty collection which should contain elements, this results in something like
// org.opentest4j.AssertionFailedError: property Home.partner is null ==> expected: not <null>
fun verifyNested(anObject:Any, context:String, fullParentName:String, parentName:String, nullAllowedProperties:List<String> = emptyList(), stopAtProperties:List<String> = emptyList(), emptyCollectionAllowedProperties:List<String> = emptyList()) {
    anObject::class.memberProperties.forEach { property ->
        val fullFQName = "$fullParentName.${property.name}"
        val fqName = "$parentName.${property.name}"
        if (fqName !in nullAllowedProperties) {
            assertNotNull(
                (property as KProperty1<Any, *>).get(anObject),
                "[$context] property $fullFQName is null"
            )
        }
        if (fqName !in stopAtProperties) {
            property.isAccessible = true
            val value = property.getter.javaMethod!!.invoke(anObject)
            value?.let {
                if (value.javaClass.simpleName in simpleTypes) {
                    // nothing more to do
                } else if (value is List<*>) {
                    if (fqName !in emptyCollectionAllowedProperties) {
                        assertNotEquals(0, value.size, "$fullFQName is an empty list ($fqName)")
                    }
                    value.forEachIndexed {i, elem -> verifyNested(elem!!, context, "$fullFQName[$i]", "$fqName[i]", nullAllowedProperties, stopAtProperties, emptyCollectionAllowedProperties) }
                } else if (value is Map<*, *>) {
                    value.forEach { key, value ->  verifyNested(value!!, context, "$fullFQName[$key]", "$fqName[*]", nullAllowedProperties, stopAtProperties, emptyCollectionAllowedProperties) }
                } else {
                    verifyNested(value, context, fullFQName, fqName, nullAllowedProperties, stopAtProperties, emptyCollectionAllowedProperties)
                }
            }
        } else {
//            println("stopped at $fqName")
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
