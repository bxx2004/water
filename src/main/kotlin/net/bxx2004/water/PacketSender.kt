package net.bxx2004.water

import java.util.*
import java.util.concurrent.CompletableFuture


interface PacketSender {
    fun sendPacket(channel: Channel, context: PacketContext, breakPoint: Boolean = false)
    fun uuid(): UUID
    fun name(): String
    fun handle(channel: Channel)
    fun postWithReturn(
        channel: Channel,
        context: PacketContext,
        breakPoint: Boolean = false
    ): CompletableFuture<PacketContext> {
        var cf = CompletableFuture<PacketContext>()
        val id = context.read<UUID>(XIdentifier("packet_info", "packet_id"))!!.toString()
        response[id] = null
        submit(-1, 1) {
            if (response[id] != null) {
                cf.complete(response[id])
                response.remove(id)
                it.cancel()
            }
        }
        sendPacket(channel, context, breakPoint)
        return cf
    }

    companion object {
        private val response = HashMap<String, PacketContext?>()

        fun init() {
            registerPacketReceiver { channel: Channel, context: PacketContext, sender: PacketSender ->
                if (context.read<UUID>("response_id") != null) {
                    if (response.containsKey(context.read<UUID>("response_id").toString())) {
                        response[context.read<UUID>("response_id")!!.toString()] = context
                    }
                }
                return@registerPacketReceiver null
            }
        }
    }
}