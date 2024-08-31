package net.bxx2004.water

import net.bxx2004.water.XIdentifier
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class PacketContextBreakPointReceiver(header: PacketContext) {
    var result = ArrayList<ByteArray>()
    var point = 0
    val id = header.read<String>(XIdentifier.of("packet_info", "breakpoint_id"))!!
    val size = header.read<Int>(XIdentifier.of("packet_info", "breakpoint_size"))!!

    init {
        breakpointCache[id] = CompletableFuture()
    }

    fun write(a: Int, byteArray: ByteArray) {
        if (a == point + 1) {
            point++
            result.add(byteArray)
            if (point >= size) {
                breakpointCache[id]!!.complete(toPacket())
            }
        }
    }

    fun canWrite(): Boolean {
        return point < size
    }

    fun toPacket(): PacketContext {
        var r = combineMultiByteArray(result)
        return PacketContext.unSerializable(r)
    }
}

val breakpointCache = ConcurrentHashMap<String, CompletableFuture<PacketContext>>()