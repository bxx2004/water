package net.bxx2004.water

import java.io.Serializable

class Channel(var identifier: XIdentifier): Serializable {
    companion object{
        const val serialVersionUID = 42L
    }
    override fun equals(other: Any?): Boolean {
        if (other is Channel) {
            return this.identifier == other.identifier
        }
        return false
    }

    override fun toString(): String {
        return identifier.toString()
    }
}
val default = Channel(XIdentifier.of("water:default"))