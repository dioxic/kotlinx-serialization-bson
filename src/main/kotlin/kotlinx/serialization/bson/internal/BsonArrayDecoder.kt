package kotlinx.serialization.bson.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.bson.Bson
import kotlinx.serialization.bson.BsonConfiguration
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.SerializersModule
import org.bson.AbstractBsonReader
import org.bson.BsonType

@ExperimentalSerializationApi
internal class BsonArrayDecoder(
    reader: AbstractBsonReader,
    serializersModule: SerializersModule,
    configuration: BsonConfiguration,
    bson: Bson,
) : DefaultBsonDecoder(reader, serializersModule, configuration, bson) {
    private var index = 0
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        val nextType = reader.readBsonType()
        if (nextType == BsonType.END_OF_DOCUMENT) return CompositeDecoder.DECODE_DONE
        return index++
    }
}