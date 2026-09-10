package mc.xalitoria.placeholder

import mc.xalitoria.serial.message.State
import org.bukkit.World
import kotlin.text.RegexOption.IGNORE_CASE
import kotlin.uuid.toKotlinUuid

val World.placeholderable get() = object : Placeholderable {

    override val placeholderPrefix: String get() = "world_"
    override fun setPlaceholders(message: String, prefix: String): String {
        val prefix = Regex.escape(prefix)
        return message
            .replace("{${prefix}name}", name, true)
            .replace("{${prefix}player_count}", playerCount.toString())
            //.replace("{${prefix}display_name}", this@placeholderable.)
            .replace("{${prefix}uuid}", uid.toString(), true)
            .replace("{${prefix}uuid_flat}", uid.toKotlinUuid().toHexString(), true)
            .replace("{${prefix}game_rule_([a-z0-9_]+?)}".toRegex(IGNORE_CASE)) { match ->
                getGameRuleValue(match.groupValues[1]).toString()
            }
            .replace("{${prefix}seed}", seed.toString(), true)
            .let { gameTime.times(50).placeholderable.setPlaceholders(it, prefix + "game_time_") }

            //.replace("{${prefix}is_fall_damage}", state.get(worldConfig.isFallDamageEnabled))
            //.replace("{${prefix}is_pvp}", state.get(worldConfig.isPvpEnabled))
            //.replace("{${prefix}is_ticking}", state.get(worldConfig.isTicking))
            //.replace("{${prefix}is_time_paused}", state.get(worldConfig.isGameTimePaused))
    }
    private val state get() = State()
}