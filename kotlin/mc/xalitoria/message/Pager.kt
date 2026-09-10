package mc.xalitoria.message

import mc.xalitoria.placeholder.Placeholderable
import kotlin.math.ceil

class Pager<T>(
    val perPage: Int = 10,
    var currentPage: Int = 1,
    vararg val elements: T
): Placeholderable {
    init { require(perPage > 0) { "perPage must be greater than 0" } }

    val totalPages = if (elements.isEmpty()) 1 else ceil(elements.size.toFloat() / perPage).toInt()

    /**
     * @param page The page number to get the range for; must be within the valid range.
     * @return The range of indices for the specified page.
     * Example: When [perPage] is 10, (0..<10) for page 1, (10..<20) for page 2, etc.
     */
    fun getRange(page: Int = currentPage): IntRange {
        val startIndex = (page.coerceIn(1, totalPages) - 1) * perPage
        return startIndex..<minOf(startIndex + perPage, elements.size)
    }

    fun getPage(page: Int = currentPage): List<T> =
        getRange(page).map { elements[it] }

    /**
     * Note: sort elements before creating a [Pager] instance.
     */
    fun getPageIndexed(page: Int = currentPage): List<Pair<Int, T>> =
        getRange(page).map { it to elements[it] }

    override fun setPlaceholders(message: String, prefix: String): String = message
        .replace("{${prefix}current_page}", currentPage.toString())
        .replace("{${prefix}last_page}", totalPages.toString())
        .replace("{${prefix}per_page}", perPage.toString())
        .replace("{${prefix}elements}", elements.size.toString())
}