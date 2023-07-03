@file:OptIn(ExperimentalContracts::class)
package kotlinx.serialization.bson

import org.bson.BsonArray
import org.bson.BsonDocument
import org.bson.BsonNull
import org.bson.BsonValue
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


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
