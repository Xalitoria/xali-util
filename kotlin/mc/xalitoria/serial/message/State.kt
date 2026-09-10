package mc.xalitoria.serial.message

import kotlinx.serialization.Serializable

@Serializable
open class State(
    open val `true`: String? = "true",
    open val `false`: String? = "false",
) {
    fun get(t: Boolean): String? = if (t) `true` else `false`
    fun setPlaceholders(message: String, state: Boolean, prefix: String = ""): String = message
        .replace("{${prefix}state}", get(state).orEmpty())
}