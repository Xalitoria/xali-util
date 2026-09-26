package mc.xalitoria.util.serial.message

import kotlinx.serialization.Serializable
import mc.xalitoria.util.message.Pager
import mc.xalitoria.util.placeholder.Placeholderable

@Serializable
data class StringList(
    val element: String,
    val header: String? = "&m-----------------------------------------------------",
    val footer: String? = "&m-----------------------------------------------------",
) {
    fun <T> setPlaceholders(paginate: Pager<T>, prefix: String = ""): String = buildString {
        header?.let { append(paginate.setPlaceholders(it)).appendLine() }
        val page = paginate.currentPage
        paginate.getPageIndexed(page).forEach { (i = first, v = second) ->
            var result = paginate.setPlaceholders(element, prefix)
                .replace("{${prefix}index}", (i + 1 * page).toString())
                .replace("{${prefix}line}", (i + 1).toString())
            if (v is Placeholderable)
                result = v.setPlaceholders(result, prefix)
            append(result).appendLine()
        }
        footer?.let { append(paginate.setPlaceholders(it)).appendLine() }
    }
}