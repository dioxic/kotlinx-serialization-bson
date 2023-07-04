package kotlinx.serialization.bson

import kotlinx.serialization.encoding.Decoder
import org.bson.*
import org.bson.types.ObjectId

/**
 * The BsonDecoder interface
 *
 * For custom serialization handlers
 */
interface BsonDecoder: Decoder {

    val bson: Bson

    /** @return the decoded ObjectId */
    fun decodeObjectId(): ObjectId

    /** @return the decoded BsonValue */
    fun decodeBsonValue(): BsonValue

    /** @return the BsonReader */
    fun reader(): BsonReader
}

