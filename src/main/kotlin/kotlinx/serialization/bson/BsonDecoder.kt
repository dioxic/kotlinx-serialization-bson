package kotlinx.serialization.bson

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encoding.Decoder
import org.bson.BsonReader
import org.bson.BsonValue
import org.bson.types.ObjectId

/**
 * The BsonDecoder interface
 *
 * For custom serialization handlers
 */
@ExperimentalSerializationApi
interface BsonDecoder: Decoder {

    val bson: Bson

    /** @return the decoded ObjectId */
    fun decodeObjectId(): ObjectId

    /** @return the decoded BsonValue */
    fun decodeBsonValue(): BsonValue

    /** @return the BsonReader */
    fun reader(): BsonReader
}

