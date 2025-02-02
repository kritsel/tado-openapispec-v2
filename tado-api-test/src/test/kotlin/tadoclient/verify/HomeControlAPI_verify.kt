package tadoclient.verify

import tadoclient.models.*

fun verifyHomeState(homeState: HomeState, context:String, parentName:String = "HomeState", ancestorObjectProps:Map<String, Any> = emptyMap()) {
    verifyObject(homeState, context, parentName, "HomeState", ancestorObjectProps)
}

