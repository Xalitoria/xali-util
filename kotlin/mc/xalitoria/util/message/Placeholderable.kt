package mc.xalitoria.util.message

@Suppress("UNCHECKED_CAST")
interface Placeholderable {
    val placeholderPrefix: String get() = ""
    fun setPlaceholders(message: String, prefix: String = placeholderPrefix): String
}