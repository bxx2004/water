package net.bxx2004.water

import java.io.*

interface PacketContext : Serializable {
    fun write(identifier: XIdentifier, value: Any, override: Boolean = false): PacketContext
    fun <T> read(identifier: XIdentifier): T?
    fun write(identifier: String, value: Any, override: Boolean = false): PacketContext
    fun <T> read(identifier: String): T?
    fun <T> readSeries(vararg identifier: String, func: (identifier: XIdentifier, value: T) -> Unit)
    fun <T> readSeries(vararg identifier: XIdentifier, func: (identifier: XIdentifier, value: T) -> Unit)
    fun forEach(func: (identifier: XIdentifier, value: Any) -> Unit)
    fun <T> remove(identifier: XIdentifier): T?
    fun <T> remove(identifier: String): T?
    fun serializable(): ByteArray {
        val out = ByteArrayOutputStream()
        val oos = ObjectOutputStream(out)
        oos.writeObject(this)
        val bytes = out.toByteArray()
        return bytes
    }

    fun size(): Int {
        return serializable().size
    }

    fun type(): PacketType {
        return read<PacketType>(XIdentifier("packet_info", "packet_type"))!!
    }

    companion object {
        const val serialVersionUID = 42L

        @JvmStatic
        fun unSerializable(packetContext: ByteArray): PacketContext {
            val ooo = ByteArrayInputStream(packetContext)
            val ois = ObjectInputStream(ooo)
            val person = ois.readObject() as PacketContext
            return person
        }

        @JvmStatic
        fun empty(type: PacketType? = null): PacketContext {
            return PacketContextImpl(type)
        }
    }
}