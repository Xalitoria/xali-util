package mc.xalitoria.util.bukkit

import mc.xalitoria.util.serial.UniqueId
import org.bukkit.entity.Player
import org.bukkit.permissions.Permissible
import java.time.ZoneId
import java.time.ZoneOffset

typealias SenderPlayer = Player
typealias TargetPlayer = Player
typealias SenderId = UniqueId
typealias TargetId = UniqueId

//val Player.zoneId: ZoneId get() = try {
//    ZoneId.of("America/New_York")
//} catch (e: Exception) {
//    ZoneId.of("UTC")
//}

/**
 * @return `true` if the [Permissible] has the permission or [permission] is `null` or empty
 */
fun Permissible.hasPermission(permission: String?): Boolean = permission?.takeIf { it.isNotEmpty() }?.let(::hasPermission) != false
fun Permissible.hasPermission(vararg permission: String): Boolean = permission.joinToString(".").let(::hasPermission)
fun Permissible.hasAnyPermission(vararg permissions: String): Boolean = permissions.any(::hasPermission)
fun Permissible.hasAnyPermission(permissions: List<String>): Boolean = permissions.any(::hasPermission)
fun Permissible.hasAnyPermission(prefix: String, suffixes: List<String>): Boolean = suffixes.any { hasPermission("$prefix.$it") }
fun Permissible.hasAnyPermission(prefix: String, aliases: List<String>, suffix: String): Boolean = aliases.any { hasPermission("$prefix.$it.$suffix") }

