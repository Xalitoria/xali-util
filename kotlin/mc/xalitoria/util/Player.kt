package mc.xalitoria.util

import mc.xalitoria.serial.XUuid
import org.bukkit.entity.Player
import org.bukkit.permissions.Permissible

typealias SenderPlayer = Player
typealias TargetPlayer = Player
typealias SenderId = XUuid
typealias TargetId = XUuid

fun Permissible.hasPermissions(vararg permissions: String): Boolean = permissions.any(::hasPermission)
fun Permissible.hasPermissions(permissions: List<String>): Boolean = permissions.any(::hasPermission)
fun Permissible.hasPermissions(prefix: String, suffixes: List<String>): Boolean = suffixes.any { hasPermission("$prefix.$it") }
fun Permissible.hasPermissions(prefix: String, aliases: List<String>, suffix: String): Boolean = aliases.any { hasPermission("$prefix.$it.$suffix") }