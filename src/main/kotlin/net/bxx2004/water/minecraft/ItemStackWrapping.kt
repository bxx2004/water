package net.bxx2004.water.minecraft

import net.bxx2004.water.XIdentifier
import java.io.*

class ItemStackWrapping : Serializable {

    companion object {
        const val serialVersionUID = 1L

        @JvmStatic
        fun unSerializable(o: ByteArray): ItemStackWrapping {
            val ooo = ByteArrayInputStream(o)
            val ois = ObjectInputStream(ooo)
            val person = ois.readObject() as ItemStackWrapping
            return person
        }
    }

    lateinit var identifier: XIdentifier
    var amount: Int = 0
    lateinit var json_tag: String
    fun serializable(): ByteArray {
        val out = ByteArrayOutputStream()
        val oos = ObjectOutputStream(out)
        oos.writeObject(this)
        val bytes = out.toByteArray()
        return bytes
    }
}