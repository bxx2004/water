package net.bxx2004.water.kcp

import com.backblaze.erasure.FecAdapt
import io.netty.buffer.ByteBuf
import kcp.ChannelConfig
import kcp.KcpClient
import kcp.Ukcp
import net.bxx2004.water.Channel
import net.bxx2004.water.kcp.impl.ClientSender
import java.net.InetSocketAddress

class Client {
    companion object{
        val client = KcpClient()
        val channels = HashMap<Channel, Ukcp>()
        private var host = ""
        private var port = 0
        fun initClient(h: String,p: Int): KcpClient{
            val config = ChannelConfig()
            config.nodelay(true,40,2,true)
            config.sndwnd = 512
            config.rcvwnd = 512
            config.mtu = 512
            config.isAckNoDelay = true
            config.conv = 55

            config.fecAdapt = FecAdapt(3,1)
            config.isCrc32Check = true
            client.init(config)
            host = h
            port = p
            return client
        }
        fun addChannel(channel: Channel,listener: ClientSender): Ukcp{
            val config = ChannelConfig()
            config.nodelay(true,40,2,true)
            config.sndwnd = 512
            config.rcvwnd = 512
            config.mtu = 512
            config.isAckNoDelay = true
            config.conv = 55

            config.fecAdapt = FecAdapt(3,1)
            config.isCrc32Check = true
            if (channels.contains(channel)){
                client.channelManager.del(channels[channel]!!)
                channels[channel]!!.close()
                channels.remove(channel)
            }
            channels[channel] = client.connect(InetSocketAddress(host,port),config,listener)
            listener.handle(channel)
            return channels[channel]!!
        }
    }
}