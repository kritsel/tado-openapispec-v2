package tadoclient.verify

import tadoclient.models.*
import kotlin.test.assertNotEquals

fun verifyUser(user: User, context:String, fullParentName:String = "User"){
    val typeName = "User"
    verifyNested(user, context, fullParentName, typeName, stopAtProperties = listOf("$typeName.mobileDevices"))

    // user's mobile devices
    assertNotEquals(0, user.mobileDevices!!.size)
    user.mobileDevices?.forEachIndexed { i, elem -> verifyMobileDevice(elem, context, "$fullParentName.mobileDevices[$i]") }
}



