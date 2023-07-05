package kotlinx.serialization.bson.fixtures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*

object CustomSerializer : KSerializer<DataClassWithSingleValue> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("DataClassWithSingleValue") {
            element<Long>("n")
        }

    override fun deserialize(decoder: Decoder): DataClassWithSingleValue =
        decoder.decodeStructure(descriptor) {
            var n: Long = -1
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> n = decodeLongElement(descriptor, 0)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            DataClassWithSingleValue(n = n)
        }

    override fun serialize(encoder: Encoder, value: DataClassWithSingleValue) =
        encoder.encodeStructure(descriptor) {
            encodeLongElement(descriptor, 0, value.n)
        }

}