package net.bxx2004.water.kcp

import com.backblaze.erasure.FecAdapt
import io.netty.buffer.ByteBuf
import kcp.ChannelConfig
import kcp.KcpListener
import kcp.KcpServer
import kcp.Ukcp
import net.bxx2004.water.Channel
import net.bxx2004.water.PacketContext
import net.bxx2004.water.PacketType
import net.bxx2004.water.callPacketReceiver
import net.bxx2004.water.kcp.impl.ServerSender
import java.util.UUID

class Server : KcpListener {
    companion object{
        val channels = HashMap<ServerSender,HashMap<Channel, Ukcp>>()
        fun initServer(listener: KcpListener,port: Int): KcpServer{
            val server = KcpServer()
            val config = ChannelConfig()
            config.nodelay(true,40,2,true)
            config.sndwnd = 512
            config.rcvwnd = 512
            config.mtu = 512
            config.isAckNoDelay = true
            config.conv = 55

            config.fecAdapt = FecAdapt(3,1)
            config.isCrc32Check = true
            server.init(listener,config,port)
            return server
        }
    }
    override fun onConnected(ukcp: Ukcp) {

    }

    override fun handleReceive(byteBuf: ByteBuf, ukcp: Ukcp) {
        val bytes = ByteArray(byteBuf.readableBytes())
        byteBuf.duplicate().readBytes(bytes)
        val p = PacketContext.unSerializable(bytes)
        if (p.type() == PacketType.HANDLE){
            val sender = ServerSender(p.read<String>("name")!!,p.read<UUID>("uuid")!!)
            if (!channels.containsKey(sender)){
                channels.put(sender, HashMap<Channel, Ukcp>())
            }
            channels[sender]!!.put(p.read<Channel>("channel")!!,ukcp)
        }else{
            channels.filter { it.value.values.map { it.user().toString() }.contains(ukcp.user().toString()) }?.let {
                val channel = it.values.find { it.values.map { it.user().toString() }.contains(ukcp.user().toString()) }?.keys?.first()!!
                callPacketReceiver(channel,p, it.keys.first())
            }
        }
    }

    override fun handleException(ex: Throwable, ukcp: Ukcp) {
        ex.printStackTrace()
    }

    override fun handleClose(ukcp: Ukcp) {
        TODO("Not yet implemented")
    }
}