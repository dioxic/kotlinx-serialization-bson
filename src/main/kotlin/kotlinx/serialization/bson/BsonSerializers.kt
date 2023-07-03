package kotlinx.serialization.bson

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import org.bson.*
import org.bson.types.ObjectId

/**
 * The default serializers module
 *
 * Handles:
 * - ObjectId serialization
 * - BsonValue serialization
 */
@ExperimentalSerializationApi
val defaultSerializersModule: SerializersModule =
    ObjectIdSerializer.serializersModule + BsonValueSerializer.serializersModule

@ExperimentalSerializationApi
@Serializer(forClass = ObjectId::class)
object ObjectIdSerializer : KSerializer<ObjectId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ObjectIdSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ObjectId) {
        when (encoder) {
            is BsonEncoder -> encoder.encodeObjectId(value)
            else -> throw SerializationException("ObjectId is not supported by ${encoder::class}")
        }
    }

    override fun deserialize(decoder: Decoder): ObjectId {
        return when (decoder) {
            is BsonDecoder -> decoder.decodeObjectId()
            else -> throw SerializationException("ObjectId is not supported by ${decoder::class}")
        }
    }

    val serializersModule: SerializersModule = SerializersModule {
        contextual(ObjectId::class, ObjectIdSerializer)
    }
}

@ExperimentalSerializationApi
@Serializer(forClass = BsonValue::class)
object BsonValueSerializer : KSerializer<BsonValue> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BsonValueSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BsonValue) {
        when (encoder) {
            is BsonEncoder -> encoder.encodeBsonValue(value)
            else -> throw SerializationException("BsonValues are not supported by ${encoder::class}")
        }
    }

    override fun deserialize(decoder: Decoder): BsonValue {
        return when (decoder) {
            is BsonDecoder -> decoder.decodeBsonValue()
            else -> throw SerializationException("BsonValues are not supported by ${decoder::class}")
        }
    }

    @Suppress("UNCHECKED_CAST")
    val serializersModule: SerializersModule = SerializersModule {
        contextual(BsonNull::class, BsonValueSerializer as KSerializer<BsonNull>)
        contextual(BsonArray::class, BsonValueSerializer as KSerializer<BsonArray>)
        contextual(BsonBinary::class, BsonValueSerializer as KSerializer<BsonBinary>)
        contextual(BsonBoolean::class, BsonValueSerializer as KSerializer<BsonBoolean>)
        contextual(BsonDateTime::class, BsonValueSerializer as KSerializer<BsonDateTime>)
        contextual(BsonDbPointer::class, BsonValueSerializer as KSerializer<BsonDbPointer>)
        contextual(BsonDocument::class, BsonValueSerializer as KSerializer<BsonDocument>)
        contextual(BsonDouble::class, BsonValueSerializer as KSerializer<BsonDouble>)
        contextual(BsonInt32::class, BsonValueSerializer as KSerializer<BsonInt32>)
        contextual(BsonInt64::class, BsonValueSerializer as KSerializer<BsonInt64>)
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
}