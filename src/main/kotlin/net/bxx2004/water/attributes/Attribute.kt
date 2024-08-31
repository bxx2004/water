package net.bxx2004.water.attributes

import java.io.Serializable

interface Attribute
private fun inferType(v:String):Any{
    v.toIntOrNull()?.let { return it }
    v.toLongOrNull()?.let { return it }
    v.toDoubleOrNull()?.let { return it }
    v.toBooleanStrictOrNull()?.let { return it }
    return v
}
data class Union<T,R>(var left:T?,var right:R?):Serializable
data class Triple<A,B,C>(var left:A,var middle:B,var right:C):Serializable
data class T20S(var x: Number, var y: Number, var z: Number) : Attribute, Serializable,Cloneable<T20S> {
    companion object {
        @JvmStatic
        fun from(label: String): T20S {
            val list = label.split(",").map { inferType(it) as Number }
            return T20S(
                list[0],
                list[1],
                list[2]
            )
        }
    }
    override fun clone():T20S{
        return T20S(x,y,z)
    }
    override fun toString(): String {
        return "${x},${y},${z}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as T20S

        if (x != other.x) return false
        if (y != other.y) return false
        return z == other.z
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

}

data class R16E(var width: Number, var height: Number) : Attribute, Serializable,Cloneable<R16E> {
    override fun toString(): String {
        return "${width},${height}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as R16E

        if (width != other.width) return false
        return height == other.height
    }

    override fun hashCode(): Int {
        var result = width.hashCode()
        result = 31 * result + height.hashCode()
        return result
    }
    override fun clone():R16E{
        return R16E(width,height)
    }
    companion object {
        @JvmStatic
        fun from(label: String): R16E {
            val list = label.split(",").map { inferType(it) as Number }
            return R16E(
                list[0],
                list[1]
            )
        }
    }

}

