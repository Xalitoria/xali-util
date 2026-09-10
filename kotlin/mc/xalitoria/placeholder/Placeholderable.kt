package mc.xalitoria.placeholder

@Suppress("UNCHECKED_CAST")
interface Placeholderable {
    val placeholderPrefix: String get() = ""
    fun setPlaceholders(message: String, prefix: String = placeholderPrefix): String
}