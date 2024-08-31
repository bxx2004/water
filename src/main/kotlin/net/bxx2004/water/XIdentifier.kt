package net.bxx2004.water

import java.io.Serializable

open class XIdentifier(var namespace: String, var path: String) : Serializable {

    override fun toString(): String {
        return "$namespace:$path"
    }

    companion object {
        @JvmStatic
        fun empty(): XIdentifier {
            return XIdentifier("", "")
        }

        @JvmStatic
        fun of(o: String): XIdentifier {
            return XIdentifier(o.split(":")[0], o.split(":").filter { it != o.split(":")[0] }.to())
        }

        @JvmStatic
        fun of(namespace: String, path: String): XIdentifier {
            return XIdentifier(namespace, path)
        }

        private fun List<String>.to(): String {
            var a = ""
            forEach {
                a += it
            }
            return a
        }
    }

    override fun equals(other: Any?): Boolean {
        return this.toString() == other.toString()
    }
}