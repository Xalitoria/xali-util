package mc.xalitoria.util.bukkit.placeholder

import mc.xalitoria.util.bukkit.SenderPlayer
import mc.xalitoria.util.bukkit.TargetPlayer
import mc.xalitoria.util.message.PLAIN
import mc.xalitoria.util.message.Placeholderable
import mc.xalitoria.util.serial.xData
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
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
            .replace("{${prefix}display_name}", LegacyComponentSerializer.legacySection().serialize(displayName()))
            .replace("{${prefix}display_name_plain}", PLAIN.serialize(displayName()))
            .replace("{${prefix}list_name}", LegacyComponentSerializer.legacySection().serialize(playerListName()))
            .replace("{${prefix}uuid_dashed}", uniqueId.toString())
            .replace("{${prefix}uuid}", uniqueId.toKotlinUuid().toHexString())
            .replace("{${prefix}unique_id}", uniqueId.toKotlinUuid().toHexString())
            .replace("{${prefix}game_mode}", gameMode.name)
            .replace("{${prefix}health}", health.toString())
            .replace("{${prefix}max_health}", (getAttribute(MAX_HEALTH)?.value ?: health).toString())
            .replace("{${prefix}absorption}", absorptionAmount.toString())
            .replace("{${prefix}food_level}", foodLevel.toString())
            .replace("{${prefix}saturation}", saturation.toString())
            .replace("{${prefix}level}", level.toString())
            .replace("{${prefix}exp}", exp.toString())
            .replace("{${prefix}total_exp}", calculateTotalExperiencePoints().toString())
            .replace("{${prefix}ping}", ping.toString())
            .replace("{${prefix}locale}", locale().toLanguageTag())
            .replace("{${prefix}client_brand}", clientBrandName ?: "unknown")
            .replace("{${prefix}walk_speed}", walkSpeed.toString())
            .replace("{${prefix}fly_speed}", flySpeed.toString())
            .replace("{${prefix}is_flying}", isFlying.toString())
            .replace("{${prefix}is_sneaking}", isSneaking.toString())
            .replace("{${prefix}is_sprinting}", isSprinting.toString())
            .replace("{${prefix}is_on_ground}", isOnGround.toString())
            .replace("{${prefix}is_op}", isOp.toString())
            .let { location.xData.setPlaceholders(it, prefix + "location_") }
            //.let { xStats?.placeholderable?.setPlaceholders(it, prefix) ?: it }

        return result
    }
}