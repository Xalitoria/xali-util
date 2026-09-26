@file:Suppress("RedundantVisibilityModifier", "NOTHING_TO_INLINE")

package mc.xalitoria.util.kotlin

fun <T> MutableCollection<T>.toggle(element: T, state: Boolean? = null): Boolean {
    val result = state ?: !contains(element)
    if (result) add(element) else remove(element)
    return result
}

public inline fun Collection<String>.stringFilter(query: String): List<String> = buildList {
    this@stringFilter.forEach {
        if (query.length <= 1) { if (it.startsWith(query, true)) add(it) }
        else { if (it.contains(query, true)) add(it) }
    }
}

public inline fun <T> Collection<T>.stringFilter(query: String, task: (T) -> String?): List<String> = buildList {
    this@stringFilter.forEach {
        val it = task(it) ?: return@forEach
        if (query.length <= 1) { if (it.startsWith(query, true)) add(it) }
        else { if (it.contains(query, true)) add(it) }
    }
}