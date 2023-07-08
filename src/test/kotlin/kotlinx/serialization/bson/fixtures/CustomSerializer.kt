package kotlinx.serialization.bson.fixtures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.bson.BsonValueSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import org.bson.BsonDocument
import org.bson.BsonValue

object CustomSerializer : KSerializer<DataClassWithCustomSerializer> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("DataClassWithCustomSerializer") {
            element("doc", BsonValueSerializer.descriptor)
            element<String>("string", isOptional = true)
            element<Int>("number")
        }

    override fun deserialize(decoder: Decoder): DataClassWithCustomSerializer =
        decoder.decodeStructure(descriptor) {
            var doc: BsonValue = BsonDocument()
            var s = "default"
            var n = -1
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> doc = decodeSerializableElement(descriptor, 0, BsonValueSerializer)
                    1 -> s = decodeStringElement(descriptor,1 )
                    2 -> n = decodeIntElement(descriptor, 2)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            DataClassWithCustomSerializer(
                doc = doc.asDocument(),
                string = s,
                number = n
            )
        }

    override fun serialize(encoder: Encoder, value: DataClassWithCustomSerializer) =
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, BsonValueSerializer, value.doc)
            encodeStringElement(descriptor, 1, value.string)
            encodeIntElement(descriptor, 2, value.number)
        }

}