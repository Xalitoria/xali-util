package mc.xalitoria.message

import mc.xalitoria.color.CodedColor
import mc.xalitoria.color.componentColors
import mc.xalitoria.color.applyColors
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import java.text.MessageFormat
import java.util.logging.Logger

var PASS_COLOR = CodedColor.LIGHT_GREEN.toString()
var INFO_COLOR = CodedColor.LIGHT_BLUE.toString()
var WARN_COLOR = CodedColor.LIGHT_YELLOW.toString()
var FAIL_COLOR = CodedColor.LIGHT_RED.toString()

fun String.formatArgs(vararg args: Any?): String = MessageFormat.format(this, *args)

fun CommandSender.send(message: String?, vararg replace: Any?) {
    message?.let { sendMessage(it.formatArgs(replace).componentColors().let { m -> MiniMessage.miniMessage().deserialize(m) }) }
}
fun CommandSender.pass(message: String, vararg replace: Any?) { send(PASS_COLOR + message, replace) }
fun CommandSender.info(message: String, vararg replace: Any?) { send(INFO_COLOR + message, replace) }
fun CommandSender.warn(message: String, vararg replace: Any?) { send(WARN_COLOR + message, replace) }
fun CommandSender.fail(message: String, vararg replace: Any?) { send(FAIL_COLOR + message, replace) }

infix fun CommandSender.send(message: String?) { message?.let { sendMessage(it.applyColors()) } }
infix fun CommandSender.pass(message: String?) { message?.let(::pass) }
infix fun CommandSender.info(message: String?) { message?.let(::info) }
infix fun CommandSender.warn(message: String?) { message?.let(::warn) }
infix fun CommandSender.fail(message: String?) { message?.let(::fail) }


fun Logger.send(message: String, vararg replace: Any?) { message.formatArgs(replace).applyColors().let(::info) }
fun Logger.pass(message: String, vararg replace: Any?) { send(PASS_COLOR + message, replace) }
fun Logger.info(message: String, vararg replace: Any?) { send(INFO_COLOR + message, replace) }
fun Logger.warn(message: String, vararg replace: Any?) { send(WARN_COLOR + message, replace) }
fun Logger.fail(message: String, vararg replace: Any?) { send(FAIL_COLOR + message, replace) }
fun Logger.fail(message: String, throwable: Throwable, vararg replace: Any?) {
    send(FAIL_COLOR + message + "\n" + throwable.stackTraceToString(), replace)
}

infix fun Logger.send(message: String?) { message?.let(::send) }
infix fun Logger.pass(message: String?) { message?.let(::pass) }
infix fun Logger.info(message: String?) { message?.let { send(INFO_COLOR + it) } } // info is shadowed by original Logger class
infix fun Logger.warn(message: String?) { message?.let(::warn) }
infix fun Logger.fail(message: String?) { message?.let(::fail) }