package mc.xalitoria.util.bukkit.placeholder

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import mc.xalitoria.util.message.Placeholderable
import org.bukkit.NamespacedKey
import org.bukkit.World
import kotlin.text.RegexOption.IGNORE_CASE
import kotlin.uuid.toKotlinUuid

val World.placeholderable get() = object : Placeholderable {
    override val placeholderPrefix: String get() = "world_"
    override fun setPlaceholders(message: String, prefix: String): String {
        val escaped = Regex.escape(prefix)
        return message
            .replace("{${prefix}name}", name, true)
            .replace("{${prefix}key}", key.asString(), true)
            .replace("{${prefix}player_count}", playerCount.toString(), true)
            .replace("{${prefix}entity_count}", entityCount.toString(), true)
            .replace("{${prefix}chunk_count}", chunkCount.toString(), true)
            .replace("{${prefix}uuid}", uid.toString(), true)
            .replace("{${prefix}uuid_flat}", uid.toKotlinUuid().toHexString(), true)
            .replace("\\{${escaped}game_rule_(.+?)\\}".toRegex(IGNORE_CASE)) { match ->
                NamespacedKey.fromString(match.groupValues[1])
                ?.let { RegistryAccess.registryAccess().getRegistry(RegistryKey.GAME_RULE)[it] }
                ?.let { getGameRuleValue(it).toString() }
                ?: return@replace match.value
            }
            .replace("{${prefix}seed}", seed.toString(), true)
            .replace("{${prefix}difficulty}", difficulty.name, true)
            .replace("{${prefix}sea_level}", seaLevel.toString(), true)
            .replace("{${prefix}view_distance}", viewDistance.toString(), true)
            .replace("{${prefix}simulation_distance}", simulationDistance.toString(), true)
            .replace("{${prefix}is_hardcore}", isHardcore.toString(), true)
            .replace("{${prefix}is_day}", isDayTime.toString(), true)
            .replace("{${prefix}is_night}", isDayTime.not().toString(), true)
            .replace("{${prefix}is_raining}", hasStorm().toString(), true)
            .replace("{${prefix}is_thundering}", isThundering.toString(), true)
            .replace("{${prefix}time}", time.toString(), true)
            .replace("{${prefix}full_time}", fullTime.toString(), true)
            .let { gameTime.times(50).placeholderable.setPlaceholders(it, prefix + "game_time_") }
    }
}
