package kotlinx.serialization.bson

import kotlinx.serialization.encoding.Encoder
import org.bson.BsonValue
import org.bson.BsonWriter
import org.bson.types.ObjectId

/**
 * The BsonEncoder interface
 *
 * For custom serialization handlers
 */
interface BsonEncoder : Encoder {

    /**
     * Encodes an ObjectId
     *
     * @param value the ObjectId
     */
    fun encodeObjectId(value: ObjectId)

    /**
     * Encodes a BsonValue
     *
     * @param value the BsonValue
     */
    fun encodeBsonValue(value: BsonValue)

    /** @return the BsonWriter */
    fun writer(): BsonWriter
}

