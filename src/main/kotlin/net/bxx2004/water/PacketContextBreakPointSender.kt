package net.bxx2004.water

import java.util.*

class PacketContextBreakPointSender(val packets: PacketContext) {
    val id = UUID.randomUUID().toString()
    var point = 0
    private var split: List<ByteArray> = splitMultiByteArray(packets.serializable())
    fun source(): PacketContext {
        return packets
    }

    fun hasNext(): Boolean {
        return point < split.size
    }

    fun next(): ByteArray {
        return split[point]
        point++
    }

    fun sendTo(channel: Channel, sender: PacketSender) {
        var p = PacketContext.empty(PacketType.BREAK_POINT_HEADER)
        p.write(XIdentifier("packet_info", "breakpoint_size"), split.size)
        p.write(XIdentifier("packet_info", "breakpoint_id"), id)
        sender.sendPacket(channel, p)
        var point = 1
        split.forEach {
            var p = PacketContext.empty(PacketType.BREAK_POINT)
            p.write(XIdentifier("packet_info", "breakpoint_id"), id)
            p.write(XIdentifier("packet_info", "breakpoint_data"), it)
            p.write("point", point)
            point++
            sender.sendPacket(channel, p)
        }
        var e = PacketContext.empty(PacketType.BREAK_POINT_FOOTER)
        e.write(XIdentifier("packet_info", "breakpoint_id"), id)
        sender.sendPacket(channel, e)
    }
}