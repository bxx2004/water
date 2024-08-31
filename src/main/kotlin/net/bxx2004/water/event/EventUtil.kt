package net.bxx2004.water.event

import net.bxx2004.water.Platform

fun register(name: String, platform: Platform, clazz: String, a: EventBuilder.() -> Unit) {
    a(EventBuilder(EventKey(name, platform, clazz)))
}

class EventBuilder(key: EventKey) {
    var map = HashMap<String, Any>()
    val event = Event(key)
    fun data(name: String, value: Any) {
        map[name] = value
    }

    fun build(): Event {
        event.data = map
        return event
    }

}