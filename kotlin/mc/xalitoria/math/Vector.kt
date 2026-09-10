@file:Suppress("NOTHING_TO_INLINE", "unused")
package mc.xalitoria.math

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.util.Vector

val Vector.destructured: XVector get() = XVector(x, y, z)

object VectorSerializer : KSerializer<Vector> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("Vector3d") {
            element<Double>("x")
            element<Double>("y")
            element<Double>("z")
        }

    override fun serialize(encoder: Encoder, value: Vector) {
        val c = encoder.beginStructure(descriptor)
        c.encodeDoubleElement(descriptor, 0, value.x)
        c.encodeDoubleElement(descriptor, 1, value.y)
        c.encodeDoubleElement(descriptor, 2, value.z)
        c.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): Vector {
        val d = decoder.beginStructure(descriptor)
        var x = 0.0
        var y = 0.0
        var z = 0.0

        while (true) when (val idx = d.decodeElementIndex(descriptor)) {
            0 -> x = d.decodeDoubleElement(descriptor, 0)
            1 -> y = d.decodeDoubleElement(descriptor, 1)
            2 -> z = d.decodeDoubleElement(descriptor, 2)
            CompositeDecoder.DECODE_DONE -> break
            else -> error("Unexpected index: $idx")
        }
        d.endStructure(descriptor)
        return Vector(x, y, z)
    }
}

typealias XVector = @Serializable(with = VectorSerializer::class) Vector