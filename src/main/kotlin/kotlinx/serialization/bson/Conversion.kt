package kotlinx.serialization.bson

import org.bson.*
import org.bson.types.Decimal128
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

private fun BsonReader.conversionException() =
    BsonInvalidOperationException(
        "Reading field '$currentName' failed, cannot convert $currentBsonType to Int"
    )

fun BsonReader.convertToInt(): Int =
    when (currentBsonType) {
        BsonType.INT32 -> readInt32()
        BsonType.INT64 -> readInt64().toInt()
        BsonType.DOUBLE -> readDouble().toInt()
        else -> throw conversionException()
    }

fun BsonReader.convertToLong(): Long =
    when (currentBsonType) {
        BsonType.INT32 -> readInt32().toLong()
        BsonType.INT64 -> readInt64()
        BsonType.DOUBLE -> readDouble().toLong()
        else -> throw conversionException()
    }

fun BsonReader.convertToDouble(): Double =
    when (currentBsonType) {
        BsonType.INT32 -> readInt32().toDouble()
        BsonType.INT64 -> readInt64().toDouble()
        BsonType.DOUBLE -> readDouble()
        else -> throw conversionException()
    }

fun Number.toBson(): BsonNumber =
    when (this) {
        is Int -> BsonInt32(this)
        is Long -> BsonInt64(this)
        is Double -> BsonDouble(this)
        is Float -> BsonDouble(this.toDouble())
        is Short -> BsonInt32(this.toInt())
        is Byte -> BsonInt32(this.toInt())
        else -> error("type of ${this::class} cannot be converted to a BsonValue")
    }

fun Int.toBson(): BsonInt32 =
    BsonInt32(this)

fun Long.toBson(): BsonInt64 =
    BsonInt64(this)

fun Double.toBson(): BsonDouble =
    BsonDouble(this)

fun Float.toBson(): BsonDouble =
    BsonDouble(this.toDouble())

fun BigDecimal.toBson(): BsonDecimal128 =
    BsonDecimal128(Decimal128(this))

fun Short.toBson(): BsonInt32 =
    BsonInt32(this.toInt())

fun Byte.toBson(): BsonInt32 =
    BsonInt32(this.toInt())

fun Boolean.toBson(): BsonBoolean =
    BsonBoolean(this)

fun String.toBson(): BsonString =
    BsonString(this)

fun Instant.toBson(): BsonDateTime =
    BsonDateTime(this.toEpochMilli())

fun List<BsonValue>.toBson(): BsonArray =
    BsonArray(this)

fun ByteArray.toBson(subType: BsonBinarySubType): BsonBinary =
    BsonBinary(subType, this)

fun ByteArray.toBson(): BsonBinary =
    BsonBinary(this)

fun UUID.toBson() : BsonBinary =
    BsonBinary(this)

fun UUID.toBson(uuidRepresentation: UuidRepresentation) : BsonBinary =
    BsonBinary(this, uuidRepresentation)

fun ObjectId.toBson(): BsonObjectId =
    BsonObjectId(this)