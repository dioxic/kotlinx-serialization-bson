package kotlinx.serialization.bson.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.bson.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.SerializersModule
import org.bson.*
import org.bson.codecs.BsonValueCodec
import org.bson.codecs.DecoderContext
import org.bson.types.ObjectId

@ExperimentalSerializationApi
internal open class DefaultBsonDecoder(
    internal val reader: AbstractBsonReader,
    override val serializersModule: SerializersModule,
    internal val configuration: BsonConfiguration,
    override val bson: Bson
) : BsonDecoder, AbstractDecoder() {

    private data class ElementMetadata(val name: String, val nullable: Boolean, var processed: Boolean = false)

    private var elementsMetadata: Array<ElementMetadata>? = null
    private var currentIndex: Int = UNKNOWN_INDEX

    companion object {
        val validKeyKinds = setOf(PrimitiveKind.STRING, PrimitiveKind.CHAR, SerialKind.ENUM)
        val bsonValueCodec = BsonValueCodec()
        const val UNKNOWN_INDEX = -10
    }

    private fun initElementMetadata(descriptor: SerialDescriptor) {
        if (this.elementsMetadata != null) return
        val elementsMetadata =
            Array(descriptor.elementsCount) {
                val elementDescriptor = descriptor.getElementDescriptor(it)
                ElementMetadata(
                    elementDescriptor.serialName, elementDescriptor.isNullable && !descriptor.isElementOptional(it)
                )
            }
        this.elementsMetadata = elementsMetadata
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        initElementMetadata(descriptor)
        currentIndex = decodeElementIndexImpl(descriptor)
        elementsMetadata?.getOrNull(currentIndex)?.processed = true
        return currentIndex
    }

    @Suppress("ReturnCount", "ComplexMethod")
    private fun decodeElementIndexImpl(descriptor: SerialDescriptor): Int {
        val elementMetadata = elementsMetadata ?: error("elementsMetadata may not be null.")
        val name: String? =
            when (reader.state ?: error("State of reader may not be null.")) {
                AbstractBsonReader.State.NAME -> reader.readName()
                AbstractBsonReader.State.VALUE -> reader.currentName
                AbstractBsonReader.State.TYPE -> {
                    reader.readBsonType()
                    return decodeElementIndexImpl(descriptor)
                }

                AbstractBsonReader.State.END_OF_DOCUMENT,
                AbstractBsonReader.State.END_OF_ARRAY ->
                    return elementMetadata.indexOfFirst { it.nullable && !it.processed }

                else -> null
            }

        return name?.let {
            val index = descriptor.getElementIndex(it)
            return if (index == CompositeDecoder.UNKNOWN_NAME) {
                reader.skipValue()
                decodeElementIndexImpl(descriptor)
            } else {
                index
            }
        }
            ?: CompositeDecoder.UNKNOWN_NAME
    }

    @Suppress("ReturnCount")
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        when (descriptor.kind) {
            is StructureKind.LIST -> {
                reader.readStartArray()
                return BsonArrayDecoder(reader, serializersModule, configuration, bson)
            }

            is PolymorphicKind -> {
                reader.readStartDocument()
                return PolymorphicDecoder(reader, serializersModule, configuration, bson)
            }

            is StructureKind.CLASS,
            StructureKind.OBJECT -> {
                val current = reader.currentBsonType
                if (current == null || current == BsonType.DOCUMENT) {
                    reader.readStartDocument()
                }
            }

            is StructureKind.MAP -> {
                reader.readStartDocument()
                return BsonDocumentDecoder(reader, serializersModule, configuration, bson)
            }

            else -> throw SerializationException("Primitives are not supported at top-level")
        }
        return DefaultBsonDecoder(reader, serializersModule, configuration, bson)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        when (descriptor.kind) {
            is StructureKind.LIST -> reader.readEndArray()
            is StructureKind.MAP,
            StructureKind.CLASS,
            StructureKind.OBJECT -> reader.readEndDocument()

            else -> super.endStructure(descriptor)
        }
    }

    override fun decodeByte(): Byte = reader.readNumber().toByte()
    override fun decodeChar(): Char = decodeString().single()
    override fun decodeFloat(): Float = reader.readNumber().toFloat()
    override fun decodeShort(): Short = reader.readNumber().toShort()
    override fun decodeBoolean(): Boolean = readOrThrow({ reader.readBoolean() }, BsonType.BOOLEAN)
    override fun decodeDouble(): Double = reader.readNumber().toDouble()
    override fun decodeInt(): Int = reader.readNumber().toInt()
    override fun decodeLong(): Long = reader.readNumber().toLong()
    override fun decodeString(): String = readOrThrow({ reader.readString() }, BsonType.STRING)

    override fun decodeNull(): Nothing? {
        if (reader.state == AbstractBsonReader.State.VALUE) {
            readOrThrow({ reader.readNull() }, BsonType.NULL)
        }
        return null
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = enumDescriptor.getElementIndex(decodeString())
    override fun decodeNotNullMark(): Boolean {
        return reader.state != AbstractBsonReader.State.END_OF_DOCUMENT && reader.currentBsonType != BsonType.NULL
    }

    override fun decodeObjectId(): ObjectId = readOrThrow({ reader.readObjectId() }, BsonType.OBJECT_ID)
    override fun decodeBsonValue(): BsonValue = bsonValueCodec.decode(reader, DecoderContext.builder().build())
    override fun reader(): BsonReader = reader

    private inline fun <T> readOrThrow(action: () -> T, bsonType: BsonType): T {
        return try {
            action()
        } catch (e: BsonInvalidOperationException) {
            throw BsonInvalidOperationException(
                "Reading field '${reader.currentName}' failed expected $bsonType type but found: ${reader.currentBsonType}.",
                e
            )
        }
    }
}