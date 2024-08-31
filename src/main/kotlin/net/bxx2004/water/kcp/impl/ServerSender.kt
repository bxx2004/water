package net.bxx2004.water.kcp.impl

import io.netty.buffer.Unpooled
import kcp.Ukcp
import net.bxx2004.water.Channel
import net.bxx2004.water.PacketContext
import net.bxx2004.water.PacketContextBreakPointSender
import net.bxx2004.water.PacketSender
import net.bxx2004.water.kcp.Client.Companion.channels
import net.bxx2004.water.kcp.Server
import java.util.UUID

class ServerSender(val name: String, val uuid: UUID) : PacketSender {
    init {
        if (!Server.channels.containsKey(this)){
            Server.channels.put(this, HashMap<Channel, Ukcp>())
        }
    }
    override fun sendPacket(
        channel: Channel,
        context: PacketContext,
        breakPoint: Boolean
    ) {
        if (!breakPoint) {
            channels[channel]?.let {
                it.write(Unpooled.wrappedBuffer(context.serializable()))
            }
        } else {
            var sendard = PacketContextBreakPointSender(context)
            sendard.sendTo(channel, this)
        }
    }

    override fun uuid(): UUID {
        return uuid
    }

    override fun name(): String {
        return name
    }

    override fun handle(channel: Channel) {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        return other is ServerSender && uuid() == other.uuid()
    }

}