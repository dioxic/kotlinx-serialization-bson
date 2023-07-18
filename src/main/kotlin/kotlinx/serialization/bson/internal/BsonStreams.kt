@file:Suppress("unused")

package kotlinx.serialization.bson.internal

import kotlinx.serialization.*
import kotlinx.serialization.bson.Bson
import org.bson.json.JsonReader
import org.bson.json.JsonWriter
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Serializes given [value] to [stream] using UTF-8 encoding and serializer retrieved from the reified type parameter.
 *
 * @throws [SerializationException] if the given value cannot be serialized to BSON.
 * @throws [IOException] If an I/O error occurs and stream cannot be written to.
 */
@ExperimentalSerializationApi
inline fun <reified T> Bson.encodeToStream(
    value: T,
    stream: OutputStream
): Unit = encodeToStream(serializersModule.serializer(), value, stream)

/**
 * Serializes the [value] with [serializer] into a [stream] using BSON format and UTF-8 encoding.
 *
 * @throws [SerializationException] if the given value cannot be serialized to BSON.
 * @throws [IOException] If an I/O error occurs and stream cannot be written to.
 */
@ExperimentalSerializationApi
fun <T> Bson.encodeToStream(
    serializer: SerializationStrategy<T>,
    value: T,
    stream: OutputStream
) {
    stream.writer().also {
        encodeByWriter(JsonWriter(it), serializer, value)
        it.flush()
    }
}

/**
 * Deserializes BSON from [stream] using UTF-8 encoding to a value of type [T] using [deserializer].
 *
 * @throws [SerializationException] if the given BSON input cannot be deserialized to the value of type [T].
 * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T]
 * @throws [IOException] If an I/O error occurs and stream cannot be read from.
 */
@ExperimentalSerializationApi
fun <T> Bson.decodeFromStream(
    deserializer: DeserializationStrategy<T>,
    stream: InputStream
): T = decodeByReader(JsonReader(stream.reader()), deserializer)

/**
 * Deserializes the contents of given [stream] to the value of type [T] using UTF-8 encoding and
 * deserializer retrieved from the reified type parameter.
 *
 * @throws [SerializationException] if the given BSON input cannot be deserialized to the value of type [T].
 * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T]
 * @throws [IOException] If an I/O error occurs and stream cannot be read from.
 */
@ExperimentalSerializationApi
inline fun <reified T> Bson.decodeFromStream(stream: InputStream): T =
    decodeFromStream(serializersModule.serializer(), stream)