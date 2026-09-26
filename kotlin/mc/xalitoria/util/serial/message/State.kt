package mc.xalitoria.util.serial.message

import kotlinx.serialization.Serializable
import mc.xalitoria.util.message.FAIL_COLOR
import mc.xalitoria.util.message.PASS_COLOR
import mc.xalitoria.util.placeholder.Placeholderable

@Serializable
open class State(
    open val `true`: String? = "true",
    open val `false`: String? = "false",
): Placeholderable {
    fun get(state: Boolean, prefix: String = placeholderPrefix): String? =
        if (state) {
            `true`?.let { PASS_COLOR + it }
        } else {
            `false`?.let { FAIL_COLOR + it }
        }?.let { setPlaceholders(it, prefix) }

    override fun setPlaceholders(message: String, prefix: String): String = message
        .replace("{${prefix}state}", message)
}