@file:OptIn(ExperimentalContracts::class)
@file:Suppress("MemberVisibilityCanBePrivate")

package kotlinx.serialization.bson

import org.bson.*
import java.math.BigDecimal
import java.time.Instant
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
     * Add `null` to a resulting BSON object using the given [key].
     */
    fun putNull(key: String) = put(key, BsonNull())

    @PublishedApi
    internal fun build(): BsonDocument = BsonDocument(content)
}

