package tadoclient.verify

import tadoclient.models.*

fun verifyUser(user: User, context:String, fullParentName:String = "User", ancestorObjectProps:Map<String, Any> = emptyMap()){
    val typeName = "User"
    verifyObject(
        user, context, fullParentName, typeName, ancestorObjectProps)
}



