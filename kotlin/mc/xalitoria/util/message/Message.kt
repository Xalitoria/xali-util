package mc.xalitoria.util.message

import mc.xalitoria.color.CodedColor
import mc.xalitoria.color.applyColors
import mc.xalitoria.color.componentColors
import mc.xalitoria.color.unescapeGradients
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.command.CommandSender
import java.util.logging.Logger

val GCOMP = GsonComponentSerializer.gson()
val MINI = MiniMessage.miniMessage()
val PLAIN = PlainTextComponentSerializer.plainText()

var PASS_COLOR = CodedColor.GREEN.toString()
var INFO_COLOR = CodedColor.BLUE.toString()
var WARN_COLOR = CodedColor.YELLOW.toString()
var FAIL_COLOR = CodedColor.RED.toString()

fun String.formatArgs(vararg args: Any?): String {
    var result = this
    args.forEachIndexed { index, arg ->
        result = result.replace("{$index}", arg.toString())
    }
    return result
}

fun String.toComponent(): Component =
    unescapeGradients()
    .applyColors()
    .componentColors()
    .let(MINI::deserialize)

private fun CommandSender._send(message: String?, vararg replace: Any?) {
    message
        ?.formatArgs(*replace)
        ?.applyColors(true)
        ?.componentColors()
        ?.let(MINI::deserialize)
        ?.let(::sendMessage)
}
fun CommandSender.send(message: String, vararg replace: Any?) { _send(message, *replace) }
fun CommandSender.pass(message: String, vararg replace: Any?) { send(PASS_COLOR + message, *replace) }
fun CommandSender.info(message: String, vararg replace: Any?) { send(INFO_COLOR + message, *replace) }
fun CommandSender.warn(message: String, vararg replace: Any?) { send(WARN_COLOR + message, *replace) }
fun CommandSender.fail(message: String, vararg replace: Any?) { send(FAIL_COLOR + message, *replace) }

infix fun CommandSender.send(message: String?) { message?.let { _send(it) } }
infix fun CommandSender.pass(message: String?) { message?.let(::pass) }
infix fun CommandSender.info(message: String?) { message?.let(::info) }
infix fun CommandSender.warn(message: String?) { message?.let(::warn) }
infix fun CommandSender.fail(message: String?) { message?.let(::fail) }


fun Logger.send(message: String, vararg replace: Any?) { message.formatArgs(*replace).toComponent().let(PLAIN::serialize).let(::info) }
fun Logger.pass(message: String, vararg replace: Any?) { send(PASS_COLOR + message, *replace) }
fun Logger.info(message: String, vararg replace: Any?) { send(INFO_COLOR + message, *replace) }
fun Logger.warn(message: String, vararg replace: Any?) { send(WARN_COLOR + message, *replace) }
fun Logger.fail(message: String, vararg replace: Any?) { send(FAIL_COLOR + message, *replace) }
fun Logger.fail(message: String, throwable: Throwable, vararg replace: Any?) {
    // The stack trace is appended after parsing: its `<init>` style names are not tags.
    fail(message + "\n" + throwable.stackTraceToString(), *replace)
}

infix fun Logger.send(message: String?) { message?.let(::send) }
infix fun Logger.pass(message: String?) { message?.let(::pass) }
infix fun Logger.info(message: String?) { message?.let { send(INFO_COLOR + it) } } // info is shadowed by original Logger class
infix fun Logger.warn(message: String?) { message?.let(::warn) }
infix fun Logger.fail(message: String?) { message?.let(::fail) }