package mc.xalitoria.config

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
        // encodeDefaults = false: fields left at their default (in particular the `null` defaults on
        // per-command messages/errors) are never written out, so the generated file only grows when an
        // admin actually adds a key. strictMode = false: keys admins add or leave behind that don't
        // match the current schema are ignored rather than rejected.
        configuration = YamlConfiguration(strictMode = false, encodeDefaults = false)
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