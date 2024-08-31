package net.bxx2004.water.example

import net.bxx2004.water.Channel
import net.bxx2004.water.PacketContext
import net.bxx2004.water.Platform
import net.bxx2004.water.XIdentifier
import net.bxx2004.water.clientSender
import net.bxx2004.water.default
import net.bxx2004.water.initWater
import net.bxx2004.water.kcp.Client
import net.bxx2004.water.kcp.impl.ServerSender
import net.bxx2004.water.packetBuilder
import net.bxx2004.water.packetInfo
import java.util.UUID

fun main() {
    //初始化
    initWater(Platform.BOTH)
}
fun c2s(){
    packetBuilder {
        receiver {channel,context,sender->
            println(channel)
            packetInfo(context)
        }
    }
    //模拟客户端向服务端发包
    clientSender.sendPacket(
        default,
        PacketContext.empty()
            .write("what is this","apple")
    )
}
fun s2c(){
    packetBuilder {
        receiver {channel,context,sender->
            println(channel)
            packetInfo(context)
        }
    }
    //模拟服务端向客户端发包
    val sender = ServerSender("bxx2004", UUID.randomUUID())
    sender.sendPacket(default, PacketContext.empty().write("answer","red apple"))
}
fun testAddChannel(){
    Client.addChannel(
        Channel(XIdentifier.of("water","test"))
    )
}