package kotlinx.serialization.bson.internal

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.bson.Bson
import kotlinx.serialization.bson.BsonConfiguration
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.SerializersModule
import org.bson.AbstractBsonReader

@OptIn(ExperimentalSerializationApi::class)
internal class PolymorphicDecoder(
    reader: AbstractBsonReader,
    serializersModule: SerializersModule,
    configuration: BsonConfiguration,
    bson: Bson,
) : DefaultBsonDecoder(reader, serializersModule, configuration, bson) {
    private var index = 0

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T =
        deserializer.deserialize(DefaultBsonDecoder(reader, serializersModule, configuration, bson))

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        return when (index) {
            0 -> index++
            1 -> index++
            else -> CompositeDecoder.DECODE_DONE
        }
    }
}