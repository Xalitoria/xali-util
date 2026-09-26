package mc.xalitoria.util.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlinx.serialization.KSerializer

class YamlFile<T>(
    private val path: Path,
    private val serializer: KSerializer<T>,
    private val default: () -> T,
    private val yaml: Yaml = Yaml(
        configuration = YamlConfiguration(strictMode = false, encodeDefaults = true)
    )
) {
    var data: T = default()
        private set

    fun load(copyDefaultFromResources: (Path) -> Unit = {}) {
        copyDefaultFromResources(path)

        data = if (!path.exists()) {
            val d = default()
            save(d)
            d
        } else {
            val text = Files.readString(path)
            yaml.decodeFromString(serializer, text)
        }
    }

    fun save(value: T = this.data) {
        path.parent?.createDirectories()
        val text = yaml.encodeToString(serializer, value)
        Files.writeString(path, text)
        this.data = value
    }
}