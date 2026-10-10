package mc.xalitoria.util.serial.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mc.xalitoria.util.bukkit.SenderPlayer
import mc.xalitoria.util.bukkit.TargetPlayer
import mc.xalitoria.util.message.send
import mc.xalitoria.util.bukkit.placeholder.PlayerPlaceholder

@Serializable
open class Recipient(
    @SerialName("to-sender")
    open val toSender: String?,
    @SerialName("to-target")
    open val toTarget: String?,
) {
    fun toSender(sender: SenderPlayer?, target: TargetPlayer?): String? =
        toSender?.let { PlayerPlaceholder.setRelational(sender, target, it) }
    fun toTarget(sender: SenderPlayer?, target: TargetPlayer?): String? =
        toTarget?.let { PlayerPlaceholder.setRelational(sender, target, it) }

    fun message(sender: SenderPlayer?, target: TargetPlayer?, format: String.() -> String = { this }) {
        sender?.send(toSender(sender, target)?.format())
        target?.send(toTarget(sender, target)?.format())
    }
}