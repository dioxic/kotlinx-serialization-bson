package kotlinx.serialization.bson.fixtures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.bson.BsonValueSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object CustomBoxedSerializer : KSerializer<CustomBoxed> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("CustomBoxed", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): CustomBoxed {
        val bsonValue = decoder.decodeSerializableValue(BsonValueSerializer)
        return CustomBoxed(bsonValue.asDocument())
    }

    override fun serialize(encoder: Encoder, value: CustomBoxed) {
        encoder.encodeSerializableValue(BsonValueSerializer, value.contents)
    }

}