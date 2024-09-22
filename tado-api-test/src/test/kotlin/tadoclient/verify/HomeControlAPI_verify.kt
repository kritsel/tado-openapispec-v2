package tadoclient.verify

import tadoclient.models.*

fun verifyHomeState(homeState: HomeState, context:String, parentName:String = "HomeState") {
    verifyNested(homeState, context, parentName, "HomeState")
}

