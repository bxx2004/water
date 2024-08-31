package net.bxx2004.water

import net.bxx2004.water.XIdentifier
import java.util.*
import java.util.concurrent.ConcurrentHashMap

interface PacketReceiver {
    fun receiveWithReturn(channel: Channel, context: PacketContext, sender: PacketSender): PacketContext?
}

var idEventBus: ConcurrentHashMap<String, PacketReceiver> = ConcurrentHashMap()
fun registerPacketReceiver(packetReceiver: PacketReceiver) {
    idEventBus[UUID.randomUUID().toString()] = packetReceiver
}

fun unregisterPackReceiver(id: String) {
    idEventBus.remove(id)
}

fun registerPacketReceiver(
    id: String,
    func: (channel: Channel, context: PacketContext, sender: PacketSender) -> PacketContext?
) {
    idEventBus[id] = object : PacketReceiver {
        override fun receiveWithReturn(channel: Channel, context: PacketContext, sender: PacketSender): PacketContext? {
            return func(channel, context, sender)
        }
    }
}

fun registerPacketReceiver(id: String, receiver: PacketReceiver) {
    idEventBus[id] = receiver
}

fun registerPacketReceiver(func: (channel: Channel, context: PacketContext, sender: PacketSender) -> PacketContext?) {
    idEventBus[UUID.randomUUID().toString()] = object : PacketReceiver {
        override fun receiveWithReturn(channel: Channel, context: PacketContext, sender: PacketSender): PacketContext? {
            return func(channel, context, sender)
        }
    }
}

private val cache = ConcurrentHashMap<String, PacketContextBreakPointReceiver>()
fun callPacketReceiver(channel: Channel, context: PacketContext, sender: PacketSender) {
    if (context.type() == PacketType.BREAK_POINT || context.type() == PacketType.BREAK_POINT_HEADER) {
        val id = context.read<String>(XIdentifier("packet_info", "breakpoint_id"))!!
        if (context.type() == PacketType.BREAK_POINT_HEADER) {
            cache.putIfAbsent(id, PacketContextBreakPointReceiver(context))
        }
        if (context.type() == PacketType.BREAK_POINT) {
            val point = context.read<Int>("point")

            cache[id]!!.write(
                point!!,
                context.read<ByteArray>(XIdentifier.of("packet_info", "breakpoint_data"))!!
            )
        }
    } else {
        idEventBus.values.forEach {
            val a = it.receiveWithReturn(channel, (context as PacketContextImpl).takeFuture(), sender)
            if (a != null) {
                a.write("response_id", context.read<UUID>(XIdentifier.of("packet_info", "packet_id"))!!)
                sender.sendPacket(channel, a, true)
            }
        }
    }
}