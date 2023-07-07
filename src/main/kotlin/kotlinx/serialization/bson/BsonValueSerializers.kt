package kotlinx.serialization.bson

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import org.bson.*
import org.bson.types.ObjectId

@Suppress("UNCHECKED_CAST")
val defaultSerializersModule = SerializersModule {
    contextual(ObjectId::class, ObjectIdSerializer)
    contextual(BsonNull::class, BsonValueSerializer as KSerializer<BsonNull>)
    contextual(BsonArray::class, BsonValueSerializer as KSerializer<BsonArray>)
    contextual(BsonBinary::class, BsonValueSerializer as KSerializer<BsonBinary>)
    contextual(BsonBoolean::class, BsonValueSerializer as KSerializer<BsonBoolean>)
    contextual(BsonDateTime::class, BsonValueSerializer as KSerializer<BsonDateTime>)
    contextual(BsonDbPointer::class, BsonValueSerializer as KSerializer<BsonDbPointer>)
    contextual(BsonDocument::class, BsonValueSerializer as KSerializer<BsonDocument>)
    contextual(BsonDouble::class, BsonValueSerializer as KSerializer<BsonDouble>)
    contextual(BsonInt32::class, BsonValueSerializer as KSerializer<BsonInt32>)
    contextual(BsonInt64::class, BsonInt64Serializer)
    contextual(BsonDecimal128::class, BsonValueSerializer as KSerializer<BsonDecimal128>)
    contextual(BsonMaxKey::class, BsonValueSerializer as KSerializer<BsonMaxKey>)
    contextual(BsonMinKey::class, BsonValueSerializer as KSerializer<BsonMinKey>)
    contextual(BsonJavaScript::class, BsonValueSerializer as KSerializer<BsonJavaScript>)
    contextual(BsonJavaScriptWithScope::class, BsonValueSerializer as KSerializer<BsonJavaScriptWithScope>)
    contextual(BsonObjectId::class, BsonValueSerializer as KSerializer<BsonObjectId>)
    contextual(BsonRegularExpression::class, BsonValueSerializer as KSerializer<BsonRegularExpression>)
    contextual(BsonString::class, BsonValueSerializer as KSerializer<BsonString>)
    contextual(BsonSymbol::class, BsonValueSerializer as KSerializer<BsonSymbol>)
    contextual(BsonTimestamp::class, BsonValueSerializer as KSerializer<BsonTimestamp>)
    contextual(BsonUndefined::class, BsonValueSerializer as KSerializer<BsonUndefined>)
    contextual(BsonDocument::class, BsonValueSerializer as KSerializer<BsonDocument>)
    contextual(RawBsonDocument::class, BsonValueSerializer as KSerializer<RawBsonDocument>)
    contextual(RawBsonArray::class, BsonValueSerializer as KSerializer<RawBsonArray>)
}

object ObjectIdSerializer : KSerializer<ObjectId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ObjectIdSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ObjectId) {
        encoder.asBsonEncoder().encodeObjectId(value)
    }

    override fun deserialize(decoder: Decoder): ObjectId =
        decoder.asBsonDecoder().decodeObjectId()

}

object BsonInt64Serializer : KSerializer<BsonInt64> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BsonInt64Serializer", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: BsonInt64) {
        encoder.encodeLong(value.longValue())
    }

    override fun deserialize(decoder: Decoder): BsonInt64 {
        return decoder.decodeLong().toBson()
    }

}

object BsonValueSerializer : KSerializer<BsonValue> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BsonValueSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BsonValue) {
        encoder.asBsonEncoder().encodeBsonValue(value)
    }

    override fun deserialize(decoder: Decoder): BsonValue =
        decoder.asBsonDecoder().decodeBsonValue()

}

fun Decoder.asBsonDecoder(): BsonDecoder = this as? BsonDecoder
    ?: throw IllegalStateException(
        "This serializer can be used only with Bson format." +
                "Expected Decoder to be BsonDecoder, got ${this::class}"
    )

fun Encoder.asBsonEncoder() = this as? BsonEncoder
    ?: throw IllegalStateException(
        "This serializer can be used only with Bson format." +
                "Expected Encoder to be BsonEncoder, got ${this::class}"
    )