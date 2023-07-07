package kotlinx.serialization.bson.fixtures

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.bson.BsonValueSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import org.bson.BsonDocument
import org.bson.BsonValue

object CustomSerializer : KSerializer<DataClassWithSingleBsonValue> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("DataClassWithSingleValue") {
            element("doc", BsonValueSerializer.descriptor)
        }

    override fun deserialize(decoder: Decoder): DataClassWithSingleBsonValue =
        decoder.decodeStructure(descriptor) {
            var doc: BsonValue = BsonDocument()
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> doc = decodeSerializableElement(descriptor, 0, BsonValueSerializer)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            DataClassWithSingleBsonValue(doc = doc.asDocument())
        }

    override fun serialize(encoder: Encoder, value: DataClassWithSingleBsonValue) =
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, BsonValueSerializer, value.doc)
        }

}