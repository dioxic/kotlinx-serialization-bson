@file:OptIn(ExperimentalContracts::class)
@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package kotlinx.serialization.bson

import org.bson.*
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Builds [BsonDocument] with the given [builderAction] builder.
 * Example of usage:
 * ```
 * val bson = buildBsonDocument {
 *     put("booleanKey", true)
 *     putBsonArray("arrayKey") {
 *         for (i in 1..10) add(i)
 *     }
 *     putBsonDocument("objectKey") {
 *         put("stringKey", "stringValue")
 *     }
 * }
 * ```
 */
@OptIn(ExperimentalContracts::class)
inline fun buildBsonDocument(builderAction: BsonDocumentBuilder.() -> Unit): BsonDocument {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    val builder = BsonDocumentBuilder()
    builder.builderAction()
    return builder.build()
}

/**
 * DSL builder for a [BsonDocument]. To create an instance of builder, use [buildBsonDocument] build function.
 */
@JsonDslMarker
class BsonDocumentBuilder @PublishedApi internal constructor() {

    private val content = mutableListOf<BsonElement>()

    /**
     * Add the given BSON [value] to a resulting BSON object using the given [key].
     */
    fun put(key: String, value: BsonValue): Boolean = content.add(BsonElement(key, value))

    /**
     * Add the given BSON [elements] to a resulting BSON object.
     */
    fun put(elements: List<BsonElement>) = content.addAll(elements)

    /**
     * Add the [BSON object][BsonDocument] produced by the [builderAction] function to a resulting BSON object using the given [key].
     */
    fun putBsonDocument(key: String, builderAction: BsonDocumentBuilder.() -> Unit) =
        put(key, buildBsonDocument(builderAction))

    /**
     * Add the [BSON array][BsonArray] produced by the [builderAction] function to a resulting BSON object using the given [key].
     */
    fun putBsonArray(key: String, builderAction: BsonArrayBuilder.() -> Unit) =
        put(key, buildBsonArray(builderAction))

    /**
     * Add the given boolean [value] to a resulting BSON object using the given [key].
     */
    fun put(key: String, value: Boolean) = put(key, value.toBson())

    /**
     * Add the given numeric [value] to a resulting BSON object using the given [key].
     */
    fun put(key: String, value: Number) = put(key, value.toBson())

    /**
     * Add the given string [value] to a resulting BSON object using the given [key].
     */
    fun put(key: String, value: String) = put(key, value.toBson())

    /**
     * Add the given instant [value] to a resulting BSON object using the given [key].
     */
    fun put(key: String, value: Instant) = put(key, value.toBson())

    /**
     * Add the given big decimal [value] to a resulting BSON object using the given [key].
     */
    fun put(key: String, value: BigDecimal) = put(key, value.toBson())

    /**
     * Add the given objectId [value] to a resulting BSON object using the given [key].
     */
    fun put(key: String, value: ObjectId) = put(key, value.toBson())

    /**
     * Add the given UUID [value] to a resulting BSON object using the given [key].
     */
    fun put(key: String, value: UUID) = put(key, value.toBson())

    /**
     * Add the given local date time [value] to a resulting BSON object using the given [key].
     */
    fun put(key: String, value: LocalDateTime) = put(key, value.toInstant(ZoneOffset.UTC))

    /**
     * Add the given local date time [value] to a resulting BSON object using the given [key].
     */
    fun put(key: String, value: LocalDate) = put(key, value.atStartOfDay().toInstant(ZoneOffset.UTC))

    /**
     * Add `null` to a resulting BSON object using the given [key].
     */
    fun putNull(key: String) = put(key, BsonNull())

    @PublishedApi
    internal fun build(): BsonDocument = BsonDocument(content)
}

/**
 * Builds [BsonArray] with the given [builderAction] builder.
 * Example of usage:
 * ```
 * val bson = buildJsonArray {
 *     add(true)
 *     addBsonArray {
 *         for (i in 1..10) add(i)
 *     }
 *     addBsonDocument {
 *         put("stringKey", "stringValue")
 *     }
 * }
 * ```
 */
@OptIn(ExperimentalContracts::class)
inline fun buildBsonArray(builderAction: BsonArrayBuilder.() -> Unit): BsonArray {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    val builder = BsonArrayBuilder()
    builder.builderAction()
    return builder.build()
}

/**
 * DSL builder for a [BsonArray]. To create an instance of builder, use [buildBsonArray] build function.
 */
@Suppress("MemberVisibilityCanBePrivate")
@JsonDslMarker
class BsonArrayBuilder @PublishedApi internal constructor() {

    private val content: MutableList<BsonValue> = mutableListOf()

    /**
     * Adds the given BSON [element] to a resulting BSON array.
     */
    fun add(element: BsonValue): Boolean {
        content += element
        return true
    }

    /**
     * Adds the given BSON [elements] to a resulting BSON array.
     *
     * @return `true` if the list was changed as the result of the operation.
     */
    fun addAll(elements: Collection<BsonValue>): Boolean =
        content.addAll(elements)

    /**
     * Adds the given boolean [value] to a resulting BSON array.
     */
    fun add(value: Boolean): Boolean = add(value.toBson())

    /**
     * Adds the given numeric [value] to a resulting BSON array.
     */
    fun add(value: Number): Boolean = add(value.toBson())

    /**
     * Adds the given string [value] to a resulting BSON array.
     */
    fun add(value: String): Boolean = add(value.toBson())

    /**
     * Adds `null` to a resulting BSON array.
     */
    fun addNull(): Boolean = add(BsonNull())

    /**
     * Adds the [BSON object][BsonDocument] produced by the [builderAction] function to a resulting BSON array.
     */
    fun addBsonDocument(builderAction: BsonDocumentBuilder.() -> Unit): Boolean =
        add(buildBsonDocument(builderAction))

    /**
     * Adds the [BSON array][BsonArray] produced by the [builderAction] function to a resulting BSON array.
     */
    fun addJsonArray(builderAction: BsonArrayBuilder.() -> Unit): Boolean =
        add(buildBsonArray(builderAction))

    /**
     * Adds the given string [values] to a resulting BSON array.
     *
     * @return `true` if the list was changed as the result of the operation.
     */
    @JvmName("addAllStrings")
    fun addAll(values: Collection<String>): Boolean =
        addAll(values.map(String::toBson))

    /**
     * Adds the given boolean [values] to a resulting BSON array.
     *
     * @return `true` if the list was changed as the result of the operation.
     */
    @JvmName("addAllBooleans")
    fun addAll(values: Collection<Boolean>): Boolean =
        addAll(values.map(Boolean::toBson))

    /**
     * Adds the given numeric [values] to a resulting BSON array.
     *
     * @return `true` if the list was changed as the result of the operation.
     */
    @JvmName("addAllNumbers")
    fun addAll(values: Collection<Number>): Boolean =
        addAll(values.map(Number::toBson))

    @PublishedApi
    internal fun build(): BsonArray = BsonArray(content)
}

@DslMarker
internal annotation class JsonDslMarker