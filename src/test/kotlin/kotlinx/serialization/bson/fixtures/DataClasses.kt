package kotlinx.serialization.bson.fixtures

import kotlinx.serialization.*
import kotlinx.serialization.bson.toBson
import org.bson.*
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.Instant
import java.util.*

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
): TestDataClass {
    companion object {
        val expectedJson = """
            { "string": "abc" }
        """.trimIndent()
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class DataClassWithEncodeDefault(
    @EncodeDefault(EncodeDefault.Mode.NEVER) val never: String = "default",
    @EncodeDefault(EncodeDefault.Mode.ALWAYS) val always: String = "default"
): TestDataClass

@Serializable
data class DataClassWithBsonValues(
    @Contextual val id: ObjectId = oid,
    @Contextual val array: BsonArray = listOf("abc".toBson()).toBson(),
    @Contextual val binary: BsonBinary = uuid.toBson(),
    @Contextual val boolean: BsonBoolean = true.toBson(),
    @Contextual val dateTime: BsonDateTime = instant.toBson(),
    @Contextual val decimal128: BsonDecimal128 = BigDecimal.ONE.toBson(),
    @Contextual val document: BsonDocument = BsonDocument("name", "Bob".toBson()),
    @Contextual val double: BsonDouble = 123.0.toBson(),
    @Contextual val int32: BsonInt32 = 123.toBson(),
    @Contextual val int64: BsonInt64 = 123L.toBson(),
    @Contextual val maxKey: BsonMaxKey = BsonMaxKey(),
    @Contextual val minKey: BsonMinKey = BsonMinKey(),
    @Contextual val objectId: BsonObjectId = oid.toBson(),
    @Contextual val string: BsonString = "abc".toBson(),
    @Contextual val timestamp: BsonTimestamp = BsonTimestamp(123, 4),
    @Contextual val undefined: BsonUndefined = BsonUndefined(),
): TestDataClass {
    companion object {
        private val oid = ObjectId("64a2a1bcac2cb9126e80d408")
        private val instant = Instant.EPOCH
        private val uuid = UUID(123, 456)
        val expectedJson = """{
           | "id": {"${'$'}oid": "64a2a1bcac2cb9126e80d408"},
           | "array": ["abc"],
           | "binary": {"${'$'}binary": {"base64": "AAAAAAAAAHsAAAAAAAAByA==", "subType": "04"}},
           | "boolean": true,
           | "dateTime": {"${'$'}date": "1970-01-01T00:00:00Z"},
           | "decimal128": {"${'$'}numberDecimal": "1"},
           | "document": {"name": "Bob"},
           | "double": 123.0,
           | "int32": 123,
           | "int64": 123,
           | "maxKey": {"${'$'}maxKey": 1},
           | "minKey": {"${'$'}minKey": 1},
           | "objectId": {"${'$'}oid": "64a2a1bcac2cb9126e80d408"},
           | "string": "abc",
           | "timestamp": {"${'$'}timestamp": {"t": 123, "i": 4}},
           | "undefined": {"${'$'}undefined": true}}
        """.trimMargin()
    }
}