package net.bxx2004.water
import java.util.*

var ValueErrorFunction = fun (value:Any):Any{
    return value
}
class PacketContextImpl(packetType: PacketType? = null) : PacketContext {
    companion object {
        const val serialVersionUID = 42L
    }

    override fun forEach(func: (identifier: XIdentifier, value: Any) -> Unit) {
        data.forEach { (t, u) ->
            func(t, u)
        }
    }

    val data = HashMap<XIdentifier, Any>()

    init {
        data[XIdentifier.of("packet_info", "packet_id")] = UUID.randomUUID()
        data[XIdentifier.of("packet_info", "packet_type")] = packetType ?: PacketType.ALL
        data[XIdentifier.of("packet_info", "time_stamp")] = System.currentTimeMillis()
    }

    override fun write(identifier: String, value: Any, override: Boolean): PacketContext {
        return write(XIdentifier("packet_data", identifier), value, override)
    }

    override fun write(identifier: XIdentifier, value: Any, override: Boolean): PacketContext {
        if (override) {
            data[identifier] = value
        } else {
            data.putIfAbsent(identifier, value)
        }
        return this
    }

    override fun <T> remove(identifier: String): T? {
        val v =data[XIdentifier("packet_data", identifier)]
        data.remove(XIdentifier("packet_data", identifier))
        return v as T
    }

    override fun <T> remove(identifier: XIdentifier): T? {
        val v =data[identifier]
        data.remove(identifier)
        return v as T
    }
    fun takeFuture(): PacketContext {
        val id = read<String>(XIdentifier("packet_data", "breakpoint_id")) ?: return this
        if (breakpointCache.containsKey(id)) {
            var a = breakpointCache[id]!!.get()
            submit(10000) {
                breakpointCache.remove(id)
            }
            return a
        } else {
            return this
        }
    }

    override fun <T> readSeries(
        vararg identifier: XIdentifier,
        func: (identifier: XIdentifier, value: T) -> Unit
    ) {
        val r = HashMap<XIdentifier, T>()
        identifier.forEach {
            r[it] = read(it)!!
        }
        r.forEach(func)
    }

    override fun <T> readSeries(vararg identifier: String, func: (identifier: XIdentifier, value: T) -> Unit) {
        val r = HashMap<XIdentifier, T>()
        identifier.forEach {
            r[XIdentifier("packet_data", it)] = read(it)!!
        }
        r.forEach(func)
    }

    override fun <T> read(identifier: String): T? {
        return read(XIdentifier("packet_data", identifier))
    }

    override fun <T> read(identifier: XIdentifier): T? {
        var r: T? = null
        data.forEach { (t, u) ->
            if (t == identifier) {
               try {
                   r = u as T
               }catch (e:Exception){
                   return ValueErrorFunction(u) as T
               }
            }
        }
        return r
    }

    override fun equals(other: Any?): Boolean {
        if (other is PacketContext) {
            return data[XIdentifier.of("packet_info", "packet_id")] == other.read(
                XIdentifier.of(
                    "packet_info",
                    "packet_id"
                )
            )
        }
        return false
    }
}