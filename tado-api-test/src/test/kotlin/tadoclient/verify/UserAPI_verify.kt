package tadoclient.verify

import tadoclient.models.*
import kotlin.test.assertNotEquals

fun verifyUser(user: User, context:String, fullParentName:String = "User", ancestorObjectProps:Map<String, Any> = emptyMap()){
    val typeName = "User"
    verifyNested(
        user, context, fullParentName, typeName, ancestorObjectProps = ancestorObjectProps)
}



