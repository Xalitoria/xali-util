package mc.xalitoria.util.serial

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mc.xalitoria.util.placeholder.Placeholderable
import mc.xalitoria.util.placeholder.placeholderable
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector
import java.math.RoundingMode
import java.text.DecimalFormat

val Location.xData: LocationData get() = LocationData(
    [x, y, z, direction.x, direction.y, direction.z],
    world?.uid
)

val Location.position: XVector get() = XVector(x, y, z)

object LocationSerializer : KSerializer<Location> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("Location") {
            element<String>("world")
            element("position", VectorSerializer.descriptor)
            element("rotation", VectorSerializer.descriptor)
        }

    override fun serialize(encoder: Encoder, value: Location) {
        val c = encoder.beginStructure(descriptor)
        c.encodeNullableSerializableElement(descriptor, 0, UuidSerializer.nullable, value.world.uid)
        c.encodeSerializableElement(descriptor, 1, VectorSerializer, value.position)
        c.encodeSerializableElement(descriptor, 2, VectorSerializer, value.direction)
        c.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): Location {
        val d = decoder.beginStructure(descriptor)
        var world: String? = null
        var position: XVector? = null
        var rotation: XVector? = null

        while (true) {
            when (val idx = d.decodeElementIndex(descriptor)) {
                0 -> world = d.decodeNullableSerializableElement(descriptor, 0, String.serializer().nullable)
                1 -> position = d.decodeSerializableElement(descriptor, 1, VectorSerializer)
                2 -> rotation = d.decodeSerializableElement(descriptor, 2, VectorSerializer)
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $idx")
            }
        }
        d.endStructure(descriptor)

        position ?: throw SerializationException("Position is null")
        return Location(
            world?.let { Bukkit.getWorld(it) },
            position.x,
            position.y,
            position.z
        ).setDirection(rotation ?: XVector())
    }
}

typealias XLocation = @Serializable(with = LocationSerializer::class) Location

@Serializable
data class LocationData(
    val coordinates: DoubleArray = DoubleArray(6),
    @SerialName("world-id")
    var worldId: UniqueId? = null,
): Placeholderable {
    var posX: Double
        get() = coordinates[0]
        set(v) { coordinates[0] = v }

    var posY: Double
        get() = coordinates[1]
        set(v) { coordinates[1] = v }

    var posZ: Double
        get() = coordinates[2]
        set(v) { coordinates[2] = v }

    var rotX: Float
        get() = coordinates[3].toFloat()
        set(v) { coordinates[3] = v.toDouble() }

    var rotY: Float
        get() = coordinates[4].toFloat()
        set(v) { coordinates[4] = v.toDouble() }

    var rotZ: Float
        get() = coordinates[5].toFloat()
        set(v) { coordinates[5] = v.toDouble() }

    var position: XVector
        get() = XVector(posX, posY, posZ)
        set(v) { posX = v.x; posY = v.y; posZ = v.z }

    var rotation: XVector
        get() = XVector(rotX, rotY, rotZ)
        set(v) { rotX = v.x.toFloat(); rotY = v.y.toFloat(); rotZ = v.z.toFloat() }

    var world: World?
        get() = worldId?.let { Bukkit.getWorld(it) }
        set(v) { worldId = v?.uid }

    /**
     * Sets the world by name.
     * @return true if the world was found, false otherwise.
     */
    fun setWorld(name: String?): Boolean {
        val world = name?.let { Bukkit.getWorld(it) }
        worldId = world?.uid
        return worldId != null
    }

    var location: XLocation
        get() = XLocation(world, posX, posY, posZ).setDirection(Vector(rotX, rotY, rotZ))
        set(v) { position = v.position; rotation = v.direction }

    override fun setPlaceholders(message: String, prefix: String): String {
        val prefix = Regex.escape(prefix)
        return message
            .replace("\\{${prefix}pos(?:ition)?_x\\}".toRegex(), posX.formatCoord())
            .replace("\\{${prefix}pos(?:ition)?_y\\}".toRegex(), posY.formatCoord())
            .replace("\\{${prefix}pos(?:ition)?_z\\}".toRegex(), posZ.formatCoord())
            .replace("\\{${prefix}rot(?:ation)?_x\\}".toRegex(), rotX.formatCoord())
            .replace("\\{${prefix}rot(?:ation)?_y\\}".toRegex(), rotY.formatCoord())
            .replace("\\{${prefix}rot(?:ation)?_z\\}".toRegex(), rotZ.formatCoord())
            .let { world?.placeholderable?.setPlaceholders(it, prefix) ?: it }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LocationData
        if (worldId != other.worldId) return false
        if (!coordinates.contentEquals(other.coordinates)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = worldId.hashCode()
        result = 31 * result + coordinates.contentHashCode()
        return result
    }

    override fun toString(): String =
        "LocationData(" +
            "world=World(" +
                "uuid=$worldId, " +
                "name=${world?.name}" +
            "), " +
            "position=Vector$position, " +
            "rotation=Vector$rotation" +
        ")"

    companion object {
        @Transient
        var df3 = DecimalFormat("#.###").apply { roundingMode = RoundingMode.HALF_UP }
        fun Double.formatCoord(): String = df3.format(this)
        fun Float.formatCoord(): String = df3.format(this)
    }
}