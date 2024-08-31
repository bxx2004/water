package net.bxx2004.water.kcp.impl

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kcp.KcpListener
import kcp.Ukcp
import net.bxx2004.water.Channel
import net.bxx2004.water.PacketContext
import net.bxx2004.water.PacketContextBreakPointSender
import net.bxx2004.water.PacketSender
import net.bxx2004.water.PacketType
import net.bxx2004.water.callPacketReceiver
import net.bxx2004.water.default

import net.bxx2004.water.kcp.Client
import net.bxx2004.water.kcp.Client.Companion.channels
import java.util.UUID

class ClientSender(val name: String, val uuid: UUID, host: String, port: Int) : PacketSender, KcpListener {
    val client = Client.initClient(host, port)
    init {
        Client.addChannel(default,this)
    }

    override fun handle(channel: Channel) {
        sendPacket(channel,PacketContext.empty(PacketType.HANDLE)
            .write("channel", channel)
            .write("name",name)
            .write("uuid",uuid),false)
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

    override fun onConnected(ukcp: Ukcp) {

    }

    override fun handleReceive(byteBuf: ByteBuf, ukcp: Ukcp) {
        val bytes = ByteArray(byteBuf.readableBytes())
        byteBuf.duplicate().readBytes(bytes)
        val context = PacketContext.unSerializable(bytes)
        val channel = context.remove<Channel>("channel")!!
        callPacketReceiver(channel,context,this)
    }

    override fun handleException(ex: Throwable, ukcp: Ukcp) {
        ex.printStackTrace()
    }

    override fun handleClose(ukcp: Ukcp) {

    }
}