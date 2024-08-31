package net.bxx2004.water.event

import net.bxx2004.water.Channel
import net.bxx2004.water.PacketSender
import net.bxx2004.water.packetBuilder
import java.io.Serializable

class Event(val key: EventKey, var data: Map<String, Any> = emptyMap()) : Serializable {
    var isCanceled: Boolean = false
    fun call(sender: PacketSender, channel: Channel, func: (isCanceled: Boolean, responseData: Map<String, Any>) -> Unit) {
        packetBuilder {
            //sender(packetSender)
            channel(channel)
            packet {
                it.write("order", "call_event")
                it.write("event", this@Event)
            }
            to {
                post(sender) {
                    val canceled = it.read<Boolean>("canceled")!!
                    val rdata = it.read<Map<String, Any>>("response_data")
                    func(canceled, rdata ?: emptyMap())
                }
            }
        }
    }

    companion object {
        const val serialVersionUID = 42L
        var isInit: Boolean = false
        var registers: (event: Event) -> Unit = {}
        fun init() {
            if (isInit) return
            packetBuilder {
                receiver { channel, context, sender ->
                    if (channel.identifier.namespace == "ares" && channel.identifier.path == "event") {
                        if (context.read<String>("order") == "call_event") {
                            val event = context.read<Event>("event")!!
                            registers(event)
                            response {
                                it.write("canceled", event.isCanceled)
                                it.write("response_data", event.data)
                            }.send(sender)
                        }
                    }
                }
            }
            isInit = true
        }
    }
}