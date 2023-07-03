package kotlinx.serialization.bson.fixtures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.bson.toBson
import org.bson.*
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class DataClassFixture<T : TestDataClass>(
    val dataClass: T,
    val expectedJson: String,
    val serializer: KSerializer<T>
)

//inline fun <reified T : TestDataClass> fixture(dataClass: T, expected: String, serializer: KSerializer<T>) =
//    DataClassFixture(
//        dataClass = dataClass,
//        expectedJson = expected,
//        serializer = serializer
//    )

val dataClassWithSimpleValues = DataClassFixture(
    dataClass = DataClassWithSimpleValues(
        boolean = false,
        byte = 123,
        char = 'A',
        double = 123.0,
        float = 123f,
        int = 123,
        long = 123L,
        short = 123,
        string = "abc",
    ),
    expectedJson = """
        { "boolean": false, "byte": 123, "char": "A", "double": 123.0, "float": 123.0, "int": 123, "long": 123, "short": 123, "string": "abc" }
    """.trimIndent(),
    serializer = DataClassWithSimpleValues.serializer()
)

private val abcList = listOf("a", "b", "c")
private val defList = listOf("d", "e", "f")
private val abcMap = mapOf("a" to 1, "b" to 2, "c" to 3)
private val defMap = mapOf("d" to 1, "e" to 2, "f" to 3)

val dataClassWithCollections = DataClassFixture(
    dataClass = DataClassWithCollections(
        listSimple = abcList,
        listList = listOf(abcList, defList),
        listMap = listOf(abcMap),
        mapSimple = abcMap,
        mapList = mapOf("a" to abcList, "b" to defList),
        mapMap = mapOf("a" to abcMap, "b" to defMap)
    ),
    expectedJson = """{
        | "listSimple": ["a", "b", "c"],
        | "listList": [["a", "b", "c"], ["d", "e", "f"]],
        | "listMap": [{"a": 1, "b": 2, "c": 3}],
        | "mapSimple": {"a": 1, "b": 2, "c": 3},
        | "mapList": {"a": ["a", "b", "c"], "b": ["d", "e", "f"]},
        | "mapMap": {"a": {"a": 1, "b": 2, "c": 3}, "b": {"d": 1, "e": 2, "f": 3}}}
    """.trimMargin(),
    serializer = DataClassWithCollections.serializer()
)

val dataClassWithNulls = DataClassFixture(
    dataClass = DataClassWithNulls(
        boolean = true,
        string = null,
        listSimple = null
    ),
    expectedJson = """
        { "boolean": true, "string": null, "listSimple": null}
    """.trimIndent(),
    serializer = DataClassWithNulls.serializer()
)

val single = DataClassWithSingleValue(string = "abc")

val dataClassWithEmbedded = DataClassFixture(
    dataClass = DataClassWithEmbedded(
        embedded = single,
        embeddedList = listOf(single),
        embeddedListList = listOf(listOf(single)),
        embeddedMap = mapOf("nested" to single),
        embeddedMapList = mapOf("nested" to listOf(single)),
        embeddedMapMap = mapOf("nested" to mapOf("nested" to single)),
    ),
    expectedJson = """{
        | "embedded": {"string": "abc"},
        | "embeddedList": [{"string": "abc"}],
        | "embeddedListList": [[{"string": "abc"}]],
        | "embeddedMap": {"nested": {"string": "abc"}},
        | "embeddedMapList": {"nested": [{"string": "abc"}]},
        | "embeddedMapMap": {"nested": {"nested": {"string": "abc"}}}}
    """.trimMargin(),
    serializer = DataClassWithEmbedded.serializer()
)

val dataClassWithSerialNames = DataClassFixture(
    dataClass = DataClassWithSerialNames(
        id = "abc",
        name = "Bob",
        string = "def",
    ),
    expectedJson = """
        { "_id": "abc", "nom": "Bob", "string": "def" }
    """.trimIndent(),
    serializer = DataClassWithSerialNames.serializer()
)

val dataClassWithEncodeDefault = DataClassFixture(
    dataClass = DataClassWithEncodeDefault(
        never = "default",
        always = "default",
    ),
    expectedJson = """
        { "always": "default" }
    """.trimIndent(),
    serializer = DataClassWithEncodeDefault.serializer()
)

private val oid = ObjectId("64a2a1bcac2cb9126e80d408")
private val instant: Instant = Instant.EPOCH
private val uuid = UUID(123, 456)

val dataClassWithBsonValues = DataClassFixture(
    dataClass = DataClassWithBsonValues(
        id = oid,
        array = listOf("abc".toBson()).toBson(),
        binary = uuid.toBson(),
        boolean = true.toBson(),
        dateTime = instant.toBson(),
        decimal128 = BigDecimal.ONE.toBson(),
        document = BsonDocument("name", "Bob".toBson()),
        double = 123.0.toBson(),
        int32 = 123.toBson(),
        int64 = 123L.toBson(),
        maxKey = BsonMaxKey(),
        minKey = BsonMinKey(),
        objectId = oid.toBson(),
        string = "abc".toBson(),
        timestamp = BsonTimestamp(123, 4),
        undefined = BsonUndefined(),
    ),
    expectedJson = """{
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
    """.trimMargin(),
    serializer = DataClassWithBsonValues.serializer()
)

val dataClassWithTransient = DataClassFixture(
    dataClass = DataClassWithTransient(
        string = "abc",
        transient = "def"
    ),
    expectedJson = """
        { "string": "abc" }
    """.trimIndent(),
    serializer = DataClassWithTransient.serializer()
)