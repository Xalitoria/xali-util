package mc.xalitoria.util.bukkit.placeholder

import mc.xalitoria.util.message.Placeholderable
import org.bukkit.entity.Player
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.text.RegexOption.IGNORE_CASE
import kotlin.time.Duration.Companion.milliseconds

val Long.placeholderable get() = object : Placeholderable {
    @Suppress("NOTHING_TO_INLINE")
    override val placeholderPrefix: String get() = "time_"
    override fun setPlaceholders(message: String, prefix: String): String =
        setPlaceholders(message, prefix, null)
    fun setPlaceholders(message: String, prefix: String = placeholderPrefix, player: Player?): String {
        val duration = this@placeholderable.milliseconds
        val escaped = Regex.escape(prefix)
        return message
            .replace(Regex("\\{${escaped}duration_(.+?)\\}", IGNORE_CASE)) { match ->
                when (match.groupValues[1].lowercase()) {
                    "years"     -> duration.inWholeDays.div(365)
                    "months"    -> duration.inWholeDays.mod(365).div(30)
                    "weeks"     -> duration.inWholeDays.mod(30).div(7)
                    "days"      -> duration.inWholeDays
                    "hours"     -> duration.inWholeHours.mod(60)
                    "minutes"   -> duration.inWholeMinutes.mod(60)
                    "seconds"   -> duration.inWholeSeconds.mod(60)
                    "ticks"     -> duration.inWholeMilliseconds.div(50).mod(20)
                    "milliseconds" ->   duration.inWholeMilliseconds
                    else -> runCatching {
                        DateTimeFormatter.ofPattern(match.groupValues[1], US)
                            .withZone(/* player.zoneId ?: */ ZoneOffset.systemDefault())
                            .format(Instant.ofEpochMilli(this@placeholderable))
                    }.getOrDefault(match.value)
                }.toString()
            }
            .replace("{${prefix}years}",    duration.inWholeDays.div(365).toString(), true)
            .replace("{${prefix}months}",   duration.inWholeDays.div(30).toString(), true)
            .replace("{${prefix}weeks}",    duration.inWholeDays.div(7).toString(), true)
            .replace("{${prefix}days}",     duration.inWholeDays.toString(), true)
            .replace("{${prefix}hours}",    duration.inWholeHours.toString(), true)
            .replace("{${prefix}minutes}",  duration.inWholeMinutes.toString(), true)
            .replace("{${prefix}seconds}",  duration.inWholeSeconds.toString(), true)
            .replace("{${prefix}ticks}",    duration.inWholeMilliseconds.div(50).toString(), true)
            .replace("{${prefix}milliseconds}", duration.inWholeMilliseconds.toString(), true)
            .replace("{${prefix}}", duration.inWholeMilliseconds.div(50).toString(), true)
    }
}