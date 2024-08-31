package net.bxx2004.water.event

import net.bxx2004.water.Platform
import java.io.Serializable

class EventKey(
    val eventName: String,
    val platform: Platform,
    val eventClass: String
) : Serializable {

    companion object {
        const val serialVersionUID = 42L
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventKey

        if (eventName != other.eventName) return false
        if (platform != other.platform) return false
        return eventClass == other.eventClass
    }

    override fun hashCode(): Int {
        var result = eventName.hashCode()
        result = 31 * result + platform.hashCode()
        result = 31 * result + eventClass.hashCode()
        return result
    }
}