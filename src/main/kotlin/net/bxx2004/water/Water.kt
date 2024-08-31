package net.bxx2004.water

import net.bxx2004.water.kcp.Server
import net.bxx2004.water.kcp.impl.ClientSender
import java.util.UUID

lateinit var clientSender: ClientSender

fun initWater(
    mode: Platform,
    port: Int = 54321,
    name_client: String="None",
    uuid_client: UUID = UUID.randomUUID(),
    host_client: String = "localhost"
){
    PacketSender.init()
    if (mode == Platform.SERVER){
        Server.initServer(Server(),port)
    }else if (mode == Platform.CLIENT){
        clientSender = ClientSender(name_client,uuid_client,host_client,port)
    }else{
        Server.initServer(Server(),port)
        clientSender = ClientSender(name_client,uuid_client,host_client,port)
    }
}