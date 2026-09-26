package mc.xalitoria.util.placeholder

import mc.xalitoria.util.bukkit.SenderPlayer
import mc.xalitoria.util.bukkit.TargetPlayer
import mc.xalitoria.util.serial.xData
import org.bukkit.entity.Player
import kotlin.uuid.toKotlinUuid

object PlayerPlaceholder {
    @Volatile var parser: (Player?, String) -> String = { player, text ->
        (player?.placeholderable?.setPlaceholders(text) ?: text)
    }
    @Volatile var relParser: (SenderPlayer?, TargetPlayer?, String) -> String = { sender, target, text ->
        text
            .let { sender?.placeholderable?.setPlaceholders(it, "sender_") ?: it }
            .let { target?.placeholderable?.setPlaceholders(it, "target_") ?: it }
    }

    fun set(player: Player?, text: String): String = parser(player, text)
    fun setRelational(sender: SenderPlayer?, target: TargetPlayer?, text: String): String = relParser(sender, target, text)
}

val Player.placeholderable get() = object : Placeholderable {
    override val placeholderPrefix: String get() = "player_"
    override fun setPlaceholders(message: String, prefix: String): String {
        val result = message
            .replace("{${prefix}name}", name)
            .replace("{${prefix}display_name}", displayName)
            .replace("{${prefix}uuid_dashed}", uniqueId.toString())
            .replace("{${prefix}uuid}", uniqueId.toKotlinUuid().toHexString())
            .replace("{${prefix}unique_id}", uniqueId.toKotlinUuid().toHexString())
            .replace("{${prefix}game_mode}", gameMode.name)
            .let { location.xData.setPlaceholders(it, prefix) }
            //.let { xStats?.placeholderable?.setPlaceholders(it, prefix) ?: it }

        return result
    }
}