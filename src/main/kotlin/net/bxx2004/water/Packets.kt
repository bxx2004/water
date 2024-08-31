package net.bxx2004.water
import java.io.ByteArrayInputStream

fun splitMultiByteArray(byteArray: ByteArray): ArrayList<ByteArray> {
    if (byteArray.size <= 10000) {
        return arrayListOf(byteArray)
    }
    var point = 0
    var r = ArrayList<ByteArray>()
    try {
        val allbyte = ByteArray(byteArray.size)
        val stream = ByteArrayInputStream(byteArray)
        stream.read(allbyte)
        val size = allbyte.size / 10000
        for (i in 0 until size) {
            point = i * 10000
            val p = ByteArray(10000)
            System.arraycopy(allbyte, point, p, 0, 10000)
            r.add(p)
        }
        point = point + 10000
        val p = ByteArray(allbyte.size - point)
        System.arraycopy(allbyte, point, p, 0, allbyte.size - point)
        r.add(p)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return r
}

fun combineMultiByteArray(data: ArrayList<ByteArray>): ByteArray {
    var r = ByteArray(data.map { it.size }.sum())
    var point = 0
    data.forEach {
        it.forEach {
            r[point] = it
            point++
        }
    }
    return r
}

fun toPacketContext(data: ArrayList<ByteArray>): PacketContext {
    return PacketContext.unSerializable(combineMultiByteArray(data))
}