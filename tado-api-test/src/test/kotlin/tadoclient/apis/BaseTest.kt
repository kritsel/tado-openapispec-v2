package tadoclient.apis

import tadoclient.TadoConfig

open class BaseTest(val tadoConfig: TadoConfig) {

    fun isHomeConfigured() : Boolean {
        return tadoConfig.home != null
    }

    fun isAirConZoneConfigured() : Boolean {
        return tadoConfig.zone != null && tadoConfig.zone!!.airCon != null
    }

    fun isHeatingZoneConfigured() : Boolean {
        return tadoConfig.zone != null && tadoConfig.zone!!.heating != null
    }

    fun isHotWaterZoneConfigured() : Boolean {
        return tadoConfig.zone != null && tadoConfig.zone!!.hotWater != null
    }

    fun isHomeAndHeatingZoneConfigured() : Boolean {
        return isHomeConfigured() && isHeatingZoneConfigured()
    }

    fun isHomeAndHotWaterZoneConfigured() : Boolean {
        return isHomeConfigured() && isHotWaterZoneConfigured()
    }

    fun isHomeAndAirConZoneConfigured() : Boolean {
        return isHomeConfigured() && isAirConZoneConfigured()
    }

    fun isThermostatDeviceConfigured() : Boolean {
        return tadoConfig.device != null && tadoConfig.device!!.thermostat != null
    }

    fun isMobileDeviceConfigured() : Boolean {
        return tadoConfig.mobileDevice != null
    }

    fun isHomeAndMobileDeviceConfigured() : Boolean {
        return isHomeConfigured() && isMobileDeviceConfigured()
    }

    fun isBridgeConfigured(): Boolean {
        return tadoConfig.bridge != null
    }

    fun isInstallationConfigured(): Boolean {
        return tadoConfig.installation != null
    }

    fun isBoilerOpenThermInterface(): Boolean {
        return tadoConfig.boiler != null && tadoConfig.boiler.interfaceType.equals("OPENTHERM")
    }
}