package kotlinx.serialization.bson.fixtures

import kotlinx.serialization.*
import kotlinx.serialization.bson.toBson
import org.bson.*
import org.bson.types.ObjectId

private typealias Simple = DataClassWithSimpleValues
private typealias Single = DataClassWithSingleValue

@Serializable
sealed interface TestDataClass

@Serializable
data class DataClassWithCollections(
    val listSimple: List<String>,
    val listList: List<List<String>>,
    val listMap: List<Map<String, Int>>,
    val mapSimple: Map<String, Int>,
    val mapList: Map<String, List<String>>,
    val mapMap: Map<String, Map<String, Int>>,
): TestDataClass

@Serializable
data class DataClassWithEmbedded(
    val embedded: Single,
    val embeddedList: List<Single>,
    val embeddedListList: List<List<Single>>,
    val embeddedMap: Map<String, Single>,
    val embeddedMapList: Map<String, List<Single>>,
    val embeddedMapMap: Map<String, Map<String,Single>>,
): TestDataClass

@Serializable
data class DataClassWithNulls(
    val boolean: Boolean?,
    val string: String?,
    val listSimple: List<String?>?
): TestDataClass

@Serializable
data class DataClassWithSimpleValues(
    val boolean: Boolean,
    val byte: Byte,
    val char: Char,
    val double: Double,
    val float: Float,
    val int: Int,
    val long: Long,
    val short: Short,
    val string: String,
): TestDataClass

@Serializable
data class DataClassWithSerialNames(
    @SerialName("_id") val id: String,
    @SerialName("nom") val name: String,
    val string: String,
): TestDataClass

@Serializable
data class DataClassWithSingleValue(
    val string: String
): TestDataClass

@Serializable
data class DataClassWithTransient(
    val string: String = "abc",
    @Transient val transient: String = "def"
): TestDataClass

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class DataClassWithEncodeDefault(
    @EncodeDefault(EncodeDefault.Mode.NEVER) val never: String = "default",
    @EncodeDefault(EncodeDefault.Mode.ALWAYS) val always: String = "default"
): TestDataClass

@Serializable
data class DataClassWithBsonValues(
    @Contextual val id: ObjectId,
    @Contextual val array: BsonArray,
    @Contextual val binary: BsonBinary,
    @Contextual val boolean: BsonBoolean,
    @Contextual val dateTime: BsonDateTime,
    @Contextual val decimal128: BsonDecimal128,
    @Contextual val document: BsonDocument,
    @Contextual val double: BsonDouble,
    @Contextual val int32: BsonInt32,
    @Contextual val int64: BsonInt64,
    @Contextual val maxKey: BsonMaxKey,
    @Contextual val minKey: BsonMinKey,
    @Contextual val objectId: BsonObjectId,
    @Contextual val string: BsonString,
    @Contextual val timestamp: BsonTimestamp,
    @Contextual val undefined: BsonUndefined,
): TestDataClass