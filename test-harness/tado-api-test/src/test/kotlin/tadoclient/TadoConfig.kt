package tadoclient

import jakarta.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.validation.annotation.Validated

@ConfigurationProperties(prefix = "tado")
@ConfigurationPropertiesScan
@Validated
data class TadoConfig (
    val home:TadoObjectLongId?,

    val zone:TadoConfigZone?,

    val device: TadoConfigDevice?,

    val mobileDevice: TadoObjectLongId?,

    @field:NotNull
    var username:String,

    @field:NotNull
    var password:String
)

data class TadoObjectIntId (
    @field:NotNull
    val id:Int
)

data class TadoObjectLongId (
    @field:NotNull
    val id:Long
)

data class TadoObjectStringId (
    @field:NotNull
    val id:String
)

data class TadoConfigZone (
    val airCon:TadoObjectIntId?,
    val heating:TadoObjectIntId?,
    val hotWater:TadoConfigZoneHotWater?
)

data class TadoConfigDevice (
    val thermostat:TadoObjectStringId?,
    val nonThermostat:TadoObjectStringId?,
)

data class TadoConfigZoneHotWater (
    @field:NotNull
    val id:Int,

    @field:NotNull
    val canSetTemperature: Boolean
)
