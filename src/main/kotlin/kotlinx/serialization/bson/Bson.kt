@file:Suppress("unused")

package kotlinx.serialization.bson

import kotlinx.serialization.*
import kotlinx.serialization.bson.internal.DefaultBsonDecoder
import kotlinx.serialization.bson.internal.DefaultBsonEncoder
import kotlinx.serialization.modules.SerializersModule
import org.bson.*
import org.bson.codecs.BsonDocumentCodec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.io.BasicOutputBuffer
import org.bson.json.JsonMode
import org.bson.json.JsonReader
import org.bson.json.JsonWriter
import java.io.StringWriter
import java.nio.ByteBuffer

/**
 * The main entry point to work with BSON serialization.
 * It is typically used by constructing an application-specific instance, with configured BSON-specific behaviour
 * and, if necessary, registered in [SerializersModule] custom serializers.
 * `Bson` instance can be configured in its `Bson {}` factory function using [BsonBuilder].
 * For demonstration purposes or trivial usages, Bson [companion][Bson.Default] can be used instead.
 *
 * Then constructed instance can be used either as regular [SerialFormat] or [StringFormat] or [BinaryFormat]
 * or for converting objects to [BsonValue] back and forth.
 *
 * This is the only serial format which has the first-class [BsonValue] support.
 * Any serializable class can be serialized to or from [BsonValue] with [Bson.decodeFromBsonDocument] and [Bson.encodeToBsonDocument] respectively or
 * serialize properties of [BsonValue] type.
 *
 * Example of usage:
 * ```
 * @Serializable
 * class DataHolder(val id: Int, val data: String, val extensions: BsonValue)
 *
 * val bson = Bson
 * val instance = DataHolder(42, "some data", buildBsonObject { put("additional key", "value") }
 *
 * // Plain StringFormat usage
 * val stringOutput: String = bson.encodeToString(instance)
 *
 * // BsonValue serialization specific for BSON only
 * val bsonDocument: BsonValue = bson.encodeToBsonValue(instance)
 *
 * // Deserialize from string
 * val deserialized: DataHolder = bson.decodeFromString<DataHolder>(stringOutput)
 *
 * // Deserialize from json tree, BSON-specific
 * val deserializedFromTree: DataHolder = bson.decodeFromBsonValue<DataHolder>(bsonDocument)
 *
 *  // Deserialize from string to BSON tree, BSON-specific
 *  val deserializedToBsonValue: BsonValue = bson.parseToBsonValue(stringOutput)
 * ```
 *
 * Bson instance also exposes its [configuration] that can be used in custom serializers
 * that rely on [BsonDecoder] and [BsonEncoder] for customizable behaviour.
 */
@ExperimentalSerializationApi
sealed class Bson(
    val configuration: BsonConfiguration,
    override val serializersModule: SerializersModule
) : StringFormat, BinaryFormat {

    private val bsonDocumentCodec = BsonDocumentCodec()

    /**
     * The default instance of [Bson] with default configuration.
     */
    companion object Default : Bson(BsonConfiguration(), defaultSerializersModule)

    /**
     * Serializes the [value] into an equivalent BSON using the given [serializer].
     *
     * @throws [SerializationException] if the given value cannot be serialized to BSON.
     */
    final override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
        val writer = StringWriter()
        bsonDocumentCodec.encode(
            writer = JsonWriter(writer, configuration.toJsonWriterSettings()),
            value = encodeToBsonDocument(serializer, value),
        )
        return writer.toString()
    }

    /**
     * Deserializes the given BSON [string] into a value of type [T] using the given [deserializer].
     *
     * @throws [SerializationException] if the given BSON string is not a valid BSON input for the type [T]
     * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T]
     */
    final override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        return decodeFromBsonDocument(
            deserializer = deserializer,
            bson = bsonDocumentCodec.decode(JsonReader(string))
        )
    }

    /**
     * Deserializes the given BSON [bytes] into a value of type [T] using the given [deserializer].
     */
    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T {
        return decodeFromBsonDocument(
            deserializer = deserializer,
            bson = bsonDocumentCodec.decode(BsonBinaryReader(ByteBuffer.wrap(bytes)))
        )
    }

    /**
     * Serializes the [value] into an equivalent BSON using the given [serializer].
     */
    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray {
        val output = BasicOutputBuffer()
        BsonDocumentCodec().encode(
            writer = BsonBinaryWriter(output, NoOpFieldNameValidator),
            value = encodeToBsonDocument(serializer, value)
        )
        return output.internalBuffer.sliceArray(0 until output.position)
    }

    /**
     * Serializes the given [value] into an equivalent [BsonValue] using the given [serializer]
     *
     * @throws [SerializationException] if the given value cannot be serialized to BSON
     */
    fun <T> encodeToBsonDocument(serializer: SerializationStrategy<T>, value: T): BsonDocument {
        val document = BsonDocument()
        val writer = BsonDocumentWriter(document)
        serializer.serialize(DefaultBsonEncoder(writer, serializersModule, configuration), value)
        writer.flush()
        return document
    }

    /**
     * Deserializes the given [bson] into a value of type [T] using the given [deserializer].
     *
     * @throws [SerializationException] if the given BSON element is not a valid BSON input for the type [T]
     * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T]
     */
    fun <T> decodeFromBsonDocument(deserializer: DeserializationStrategy<T>, bson: BsonDocument): T {
        val reader = bson.asBsonReader()
        require(reader is AbstractBsonReader)
        return deserializer.deserialize(DefaultBsonDecoder(reader, serializersModule, configuration, this))
    }

    internal fun <T> Bson.encodeByWriter(writer: BsonWriter, serializer: SerializationStrategy<T>, value: T) {
        bsonDocumentCodec.encode(
            writer = writer,
            value = encodeToBsonDocument(serializer, value),
        )
    }

    internal fun <T> Bson.decodeByReader(reader: BsonReader, deserializer: DeserializationStrategy<T>): T =
        decodeFromBsonDocument(
            deserializer = deserializer,
            bson = bsonDocumentCodec.decode(reader)
        )

    /**
     * Deserializes the given BSON [string] into a corresponding [BsonValue] representation.
     *
     * @throws [SerializationException] if the given string is not a valid BSON
     */
    fun parseToBsonValue(string: String): BsonValue {
        return decodeFromString(BsonValueSerializer, string)
    }
}

/**
 * Creates an instance of [Bson] configured from the optionally given [Bson instance][from] and adjusted with [builderAction].
 */
@ExperimentalSerializationApi
fun Bson(from: Bson = Bson, builderAction: BsonBuilder.() -> Unit): Bson {
    val builder = BsonBuilder(from)
    builder.builderAction()
    val conf = builder.build()
    return BsonImpl(conf, builder.serializersModule)
}

/**
 * Serializes the given [value] into an equivalent [BsonDocument] using a serializer retrieved
 * from reified type parameter.
 *
 * @throws [SerializationException] if the given value cannot be serialized to BSON.
 */
@ExperimentalSerializationApi
inline fun <reified T> Bson.encodeToBsonDocument(value: T): BsonDocument {
    return encodeToBsonDocument(serializersModule.serializer(), value)
}

/**
 * Deserializes the given [bson] element into a value of type [T] using a deserializer retrieved
 * from reified type parameter.
 *
 * @throws [SerializationException] if the given BSON element is not a valid BSON input for the type [T]
 * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T]
 */
@ExperimentalSerializationApi
inline fun <reified T> Bson.decodeFromBsonDocument(bson: BsonDocument): T =
    decodeFromBsonDocument(serializersModule.serializer(), bson)

/**
 * Builder of the [Bson] instance provided by `Bson { ... }` factory function.
 */
@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalSerializationApi
class BsonBuilder internal constructor(bson: Bson) {
    /**
     * Specifies whether default values of Kotlin properties should be encoded.
     * `false` by default.
     */
    var encodeDefaults: Boolean = bson.configuration.encodeDefaults

    /**
     * Specifies whether `null` values should be encoded for nullable properties and must be present in BSON object
     * during decoding.
     *
     * When this flag is disabled properties with `null` values without default are not encoded;
     * during decoding, the absence of a field value is treated as `null` for nullable properties without a default value.
     *
     * `true` by default.
     */
    var explicitNulls: Boolean = bson.configuration.explicitNulls

    /**
     * Specifies whether encounters of unknown properties in the input BSON
     * should be ignored instead of throwing [SerializationException].
     * `false` by default.
     */
    var ignoreUnknownKeys: Boolean = bson.configuration.ignoreUnknownKeys

    /**
     * Removes BSON specification restriction (RFC-4627) and makes parser
     * more liberal to the malformed input. In lenient mode quoted boolean literals,
     * and unquoted string literals are allowed.
     *
     * Its relaxations can be expanded in the future, so that lenient parser becomes even more
     * permissive to invalid value in the input, replacing them with defaults.
     *
     * `false` by default.
     */
    var jsonMode: JsonMode = bson.configuration.jsonMode

    /**
     * Specifies whether resulting BSON should be pretty-printed.
     *  `false` by default.
     */
    var prettyPrint: Boolean = bson.configuration.prettyPrint

    /**
     * Specifies indent string to use with [prettyPrint] mode
     * 4 spaces by default.
     * Experimentality note: this API is experimental because
     * it is not clear whether this option has compelling use-cases.
     */
    var prettyPrintIndent: String = bson.configuration.prettyPrintIndent

    /**
     * Name of the class descriptor property for polymorphic serialization.
     * "type" by default.
     */
    var classDiscriminator: String = bson.configuration.classDiscriminator

    var newLineCharacters: String = bson.configuration.newLineCharacters

    /**
     * Module with contextual and polymorphic serializers to be used in the resulting [Bson] instance.
     *
     * @see SerializersModule
     * @see Contextual
     * @see Polymorphic
     */
    var serializersModule: SerializersModule = bson.serializersModule

    internal fun build(): BsonConfiguration {
        if (!prettyPrint) {
            require(prettyPrintIndent == defaultIndent) {
                "Indent should not be specified when default printing mode is used"
            }
        } else if (prettyPrintIndent != defaultIndent) {
            // Values allowed by BSON specification as whitespaces
            val allWhitespaces = prettyPrintIndent.all { it == ' ' || it == '\t' || it == '\r' || it == '\n' }
            require(allWhitespaces) {
                "Only whitespace, tab, newline and carriage return are allowed as pretty print symbols. Had $prettyPrintIndent"
            }
        }

        return BsonConfiguration(
            encodeDefaults = encodeDefaults,
            ignoreUnknownKeys = ignoreUnknownKeys,
            jsonMode = jsonMode,
            newLineCharacters = newLineCharacters,
            prettyPrint = prettyPrint,
            explicitNulls = explicitNulls,
            prettyPrintIndent = prettyPrintIndent,
            classDiscriminator = classDiscriminator
        )
    }
}

@ExperimentalSerializationApi
private class BsonImpl(configuration: BsonConfiguration, module: SerializersModule) : Bson(configuration, module)

object NoOpFieldNameValidator : FieldNameValidator {
    override fun validate(fieldName: String?) = true

    override fun getValidatorForField(fieldName: String?) = this
}

internal fun BsonDocumentCodec.decode(reader: BsonReader) =
    this.decode(reader, DecoderContext.builder().build())

internal fun BsonDocumentCodec.encode(writer: BsonWriter, value: BsonDocument) =
    this.encode(writer, value, EncoderContext.builder().build())

private const val defaultIndent = "  "
private const val defaultDiscriminator = "type"
