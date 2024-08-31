package net.bxx2004.water

import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import kotlin.concurrent.thread

fun submit(delay: Int = -1, period: Int = -1, consumer: Consumer<TimerTask>) {
    val timer = Timer()
    if (delay == -1 && period == -1) {
        timer.schedule(object : TimerTask() {
            override fun run() {
                consumer.accept(this)
            }
        }, 0)
        return
    }
    if (delay == -1) {
        timer.schedule(object : TimerTask() {
            override fun run() {
                consumer.accept(this)
            }
        }, 0, period.toLong())
    }
    if (period == -1) {
        timer.schedule(object : TimerTask() {
            override fun run() {
                consumer.accept(this)
            }
        }, delay.toLong())
    }
    if (period != -1 && delay != -1) {
        timer.schedule(object : TimerTask() {
            override fun run() {
                consumer.accept(this)
            }
        }, delay.toLong(), period.toLong())
    }

}

fun packetInfo(packet: PacketContext, level: Int = 1, filter: Boolean = true) {
    var tab = ""
    for (i in 0..level) {
        tab += "  "
    }
    packet.forEach { identifier, value ->
        if (value is PacketContext) {
            info(tab + identifier.path + "= {")
            packetInfo(value, level + 1)
            info("$tab}")
        } else {
            if (filter) {
                if (identifier.path == "packet_id" || identifier.path == "packet_type" || identifier.path == "time_stamp") {

                } else {
                    info(tab + identifier.path + "= " + value)
                }
            } else {
                info(tab + identifier.path + "= " + value)
            }
        }
    }
}

fun info(value: Any) {
    println("[Packets-INFO] => $value")
}

fun warn(value: Any) {
    println("[Packets-WARN] => $value")
}

fun error(value: Any) {
    println("[Packets-ERROR] => $value")
}

fun <T> timeout(
    data: CompletableFuture<T>,
    message: String = "Timed out and did not respond when attempting to grab the response packet.",
    outTime: () -> Unit = {},
    func: (t: T) -> Unit
) {
    var result: T? = null
    var th = newThread {
        result = data.get()
        func(result!!)
    }
    submit(delay = 10000) {
        if (result == null) {
            warn(message)
            outTime()
        }
        it.cancel()
    }
}

fun packetBuilder(func: PacketUtil.() -> Unit) {
    func(PacketUtil())
}

class PacketUtil {
    private var channel: Channel? = null
    private var packet: PacketContext? = null
    fun channel(channel: Channel) {
        this.channel = channel
    }

    fun packet(func: (packet: PacketContext) -> Unit) {
        val it = PacketContext.empty()
        func(it)
        this.packet = it
    }

    fun to(func: SendUtil.() -> Unit) {
        func(SendUtil(packet!!, channel!!))
    }

    fun receiver(func: ResponseUtil.(channel: Channel, context: PacketContext, sender: PacketSender) -> Unit) {
        registerPacketReceiver { channel, context, sender ->
            var senderUtil = ResponseUtil(context, channel)
            func(senderUtil, channel, context, sender)
            return@registerPacketReceiver null
        }
    }

    fun receiver(channel: Channel, func: ResponseUtil.(order: String, context: PacketContext) -> Unit) {
        registerPacketReceiver { c, context, sender ->
            if (c == channel) {
                var senderUtil = ResponseUtil(context, channel)
                if (context.read<String>("order") != null) {
                    func(senderUtil, context.read<String>("order")!!, context)
                }
            }
            return@registerPacketReceiver null
        }
    }

    fun receiver(channel: Channel, order: String, func: ResponseUtil.(context: PacketContext) -> Unit) {
        registerPacketReceiver { c, context, sender ->
            if (c == channel) {
                var senderUtil = ResponseUtil(context, channel)
                if (context.read<String>("order") == order) {
                    func(senderUtil, context)
                }
            }
            return@registerPacketReceiver null
        }
    }

}

class ResponseUtil(val spacket: PacketContext, val channel: Channel) {
    fun response(func: (context: PacketContext) -> Unit): SendUtil {
        val it = PacketContext.empty()
            .write("response_id", spacket.read<UUID>(XIdentifier("packet_info", "packet_id"))!!)
        func(it)
        return SendUtil(it, channel)
    }
}

class SendUtil(val packet: PacketContext, val channel: Channel) {
    fun send(sender: PacketSender) {
        sender.sendPacket(channel, packet)
    }

    fun post(sender: PacketSender,func: (p: PacketContext) -> Unit) {
        timeout(
            sender.postWithReturn(channel, packet),
            "获取包${packet.read<UUID>(XIdentifier("packet_info", "packet_id"))}超时"
        ) {
            func(it)
        }
    }
}

fun newThread(func: () -> Unit): Thread {
    return thread(start = true) {
        func()
    }
}

fun currentThread(func: () -> Unit) {
    func()
}