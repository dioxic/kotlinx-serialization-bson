package kotlinx.serialization.bson.fixtures

import kotlinx.serialization.*
import org.bson.*
import org.bson.types.ObjectId

private typealias Single = DataClassWithSingleValue

@Serializable
data class DataClassWithCollections(
    val listSimple: List<String>,
    val listList: List<List<String>>,
    val listMap: List<Map<String, Int>>,
    val mapSimple: Map<String, Int>,
    val mapList: Map<String, List<String>>,
    val mapMap: Map<String, Map<String, Int>>,
)

@Serializable
data class DataClassWithEmbedded(
    val embedded: Single,
    val embeddedList: List<Single>,
    val embeddedListList: List<List<Single>>,
    val embeddedMap: Map<String, Single>,
    val embeddedMapList: Map<String, List<Single>>,
    val embeddedMapMap: Map<String, Map<String, Single>>,
)

@Serializable
data class DataClassWithNulls(
    val boolean: Boolean?,
    val string: String?,
    val listSimple: List<String?>?
)

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
)

@Serializable
data class DataClassWithSerialNames(
    @SerialName("_id") @Contextual val id: ObjectId,
    @SerialName("nom") val name: String,
    val string: String,
)

@Serializable
data class DataClassWithSingleValue(
    val n: Long
)

@Serializable(with = CustomSerializer::class)
data class DataClassWithCustomSerializer(
    val doc: BsonDocument,
    val string: String = "default",
    val number: Int
)

@Serializable
data class DataClassWithTransient(
    val string: String,
    @Transient val transient: String = "def"
)

@Serializable
data class DataClassWithEncodeDefault(
    @EncodeDefault(EncodeDefault.Mode.NEVER) val never: String = "default",
    @EncodeDefault(EncodeDefault.Mode.ALWAYS) val always: String = "default"
)

@Serializable
data class DataClassWithBsonValues(
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
)

@Serializable
data class DataClassWithParty(
    val party: Party
)

@Serializable(PartySerializer::class)
sealed interface Party {
    val name: String
}

@Serializable
data class Organisation(
    override val name: String,
    val companyId: Int,
) : Party

@Serializable
data class Individual(
    override val name: String,
    val height: Int,
) : Party

@Serializable
data class Boxed<T>(val contents: T)

@Serializable(with = CustomBoxedSerializer::class)
data class CustomBoxed(
    val contents: BsonDocument
)