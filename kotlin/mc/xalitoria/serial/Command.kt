package mc.xalitoria.serial

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder 
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

object CommandSerializer : KSerializer<Command> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("Command") {
            element<String>("name")
            element<String>("description")
            element<String>("usage")
            element<String?>("permission")
            element("aliases", ListSerializer(String.serializer()).descriptor)
        }

    override fun serialize(encoder: Encoder, value: Command) {
        val e = encoder.beginStructure(descriptor)
        e.encodeStringElement(descriptor, 0, value.name)
        e.encodeStringElement(descriptor, 1, value.description)
        e.encodeStringElement(descriptor, 2, value.usage)
        e.encodeNullableSerializableElement(descriptor, 3, String.serializer().nullable, value.permission)
        e.encodeSerializableElement(descriptor, 4, ListSerializer(String.serializer()), value.aliases)
        e.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): Command {
        val d = decoder.beginStructure(descriptor)
        var name: String? = null
        var description = ""
        var usage = "/<command>"
        var permission: String? = null
        var aliases = emptyList<String>()

        while (true) {
            when (val idx = d.decodeElementIndex(descriptor)) {
                0 -> name = d.decodeStringElement(descriptor, 0)
                1 -> description = d.decodeStringElement(descriptor, 1)
                2 -> usage = d.decodeStringElement(descriptor, 2)
                3 -> permission = d.decodeNullableSerializableElement(descriptor, 3, String.serializer().nullable)
                4 -> aliases = d.decodeSerializableElement(descriptor, 4, ListSerializer(String.serializer()))
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $idx")
            }
        }
        d.endStructure(descriptor)

        name ?: throw SerializationException("Name is null")
        return object : Command(name, description, usage, aliases) {
            init {
                this.permission = permission
            }
            override fun execute(sender: CommandSender, label: String, args: Array<String>) = false
        }.apply {
            setPermission(permission)
        }
    }
}

typealias BukkitCommand = @Serializable(with = CommandSerializer::class) Command