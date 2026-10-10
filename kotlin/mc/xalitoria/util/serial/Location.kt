package mc.xalitoria.util.serial

import kotlinx.serialization.*
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mc.xalitoria.util.message.Placeholderable
import mc.xalitoria.util.bukkit.placeholder.placeholderable
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.*

val Location.xData: LocationData get() = LocationData(
    [x, y, z, direction.x, direction.y, direction.z],
).apply { world = this@xData.world }

val Location.position: XVector get() = XVector(x, y, z)

@OptIn(ExperimentalSerializationApi::class)
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

    @SerialName("world-name")
    var worldName: String? = null
        private set

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
        get() {
            val result = worldId?.let { Bukkit.getWorld(it) } ?: worldName?.let { Bukkit.getWorld(it) }
            worldId = result?.uid
            worldName = result?.name ?: worldName
            return result
        }
        set(v) {
            worldId = v?.uid
            worldName = v?.name ?: worldName
        }

    var pitch: Float
        get() {
            return if (rotX == 0.0F && rotZ == 0.0F) { if (rotY > 0) -90 else 90 }
            else { atan(-rotY.div(sqrt(rotX*rotX + rotZ*rotZ))).times(RADIANS_TO_DEGREES) }.toFloat()
        }
        set(v) = setDirection(pitchDeg = v)

    var yaw: Float
        get() {
            val twoPI = 2 * Math.PI
            return atan2(-rotX, rotZ).plus(twoPI).mod(twoPI).times(RADIANS_TO_DEGREES).toFloat()
        }
        set(v) = setDirection(yawDeg = v)

    /** Rebuilds the direction vector from [yawDeg]/[pitchDeg] together, since neither angle alone determines it. */
    private fun setDirection(yawDeg: Float = yaw, pitchDeg: Float = pitch) {
        val yawRad = yawDeg.div(RADIANS_TO_DEGREES)
        val pitchRad = pitchDeg.div(RADIANS_TO_DEGREES)
        val xz = cos(pitchRad)
        rotX = -sin(yawRad).times(xz).toFloat()
        rotY = -sin(pitchRad).toFloat()
        rotZ = cos(yawRad).times(xz).toFloat()
    }

    var location: XLocation
        get() = XLocation(world, posX, posY, posZ).setDirection(XVector(rotX, rotY, rotZ))
        set(v) { position = v.position; rotation = v.direction }

    override fun setPlaceholders(message: String, prefix: String): String {
        val escaped = Regex.escape(prefix)
        return message
            .replace("\\{${escaped}pos(?:ition)?_x\\}".toRegex(), posX.formatCoord())
            .replace("\\{${escaped}pos(?:ition)?_y\\}".toRegex(), posY.formatCoord())
            .replace("\\{${escaped}pos(?:ition)?_z\\}".toRegex(), posZ.formatCoord())
            .replace("\\{${escaped}rot(?:ation)?_x\\}".toRegex(), rotX.formatCoord())
            .replace("\\{${escaped}rot(?:ation)?_y\\}".toRegex(), rotY.formatCoord())
            .replace("\\{${escaped}rot(?:ation)?_z\\}".toRegex(), rotZ.formatCoord())
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
        private const val RADIANS_TO_DEGREES = 57.29577951308232
        @Transient
        var df3 = DecimalFormat("#.###").apply { roundingMode = RoundingMode.HALF_UP }
        fun Double.formatCoord(): String = df3.format(this)
        fun Float.formatCoord(): String = df3.format(this)
    }
}