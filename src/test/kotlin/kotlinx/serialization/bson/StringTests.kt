package kotlinx.serialization.bson

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import kotlinx.serialization.bson.fixtures.*
import kotlinx.serialization.decodeFromString
import org.bson.*
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.Instant
import java.util.*

class StringTests : FunSpec({

    val abcList = listOf("a", "b", "c")
    val defList = listOf("d", "e", "f")
    val abcMap = mapOf("a" to 1, "b" to 2, "c" to 3)
    val defMap = mapOf("d" to 1, "e" to 2, "f" to 3)

    include(
        stringTest(
            name = "data class with simple values",
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
            json = """
                { "boolean": false, "byte": 123, "char": "A", "double": 123.0, "float": 123.0, "int": 123, "long": 123, "short": 123, "string": "abc" }
            """.trimIndent()
        )
    )

    include(
        stringTest(
            name = "data class with collections",
            dataClass = DataClassWithCollections(
                listSimple = abcList,
                listList = listOf(abcList, defList),
                listMap = listOf(abcMap),
                mapSimple = abcMap,
                mapList = mapOf("x" to abcList, "y" to defList),
                mapMap = mapOf("x" to abcMap, "y" to defMap)
            ),
            json = """{
                | "listSimple": ["a", "b", "c"],
                | "listList": [["a", "b", "c"], ["d", "e", "f"]],
                | "listMap": [{"a": 1, "b": 2, "c": 3}],
                | "mapSimple": {"a": 1, "b": 2, "c": 3},
                | "mapList": {"x": ["a", "b", "c"], "y": ["d", "e", "f"]},
                | "mapMap": {"x": {"a": 1, "b": 2, "c": 3}, "y": {"d": 1, "e": 2, "f": 3}}}
            """.trimMargin()
        )
    )

    include(
        stringTest(
            name = "data class with nulls",
            dataClass = DataClassWithNulls(
                boolean = true,
                string = null,
                listSimple = null
            ),
            json = """
                { "boolean": true, "string": null, "listSimple": null}
            """.trimIndent()
        )
    )

    val single = DataClassWithSingleValue(n = 123)

    include(
        stringTest(
            name = "data class with embedded",
            dataClass = DataClassWithEmbedded(
                embedded = single,
                embeddedList = listOf(single),
                embeddedListList = listOf(listOf(single)),
                embeddedMap = mapOf("nested" to single),
                embeddedMapList = mapOf("nested" to listOf(single)),
                embeddedMapMap = mapOf("nested" to mapOf("nested" to single)),
            ),
            json = """{
                | "embedded": {"n": 123},
                | "embeddedList": [{"n": 123}],
                | "embeddedListList": [[{"n": 123}]],
                | "embeddedMap": {"nested": {"n": 123}},
                | "embeddedMapList": {"nested": [{"n": 123}]},
                | "embeddedMapMap": {"nested": {"nested": {"n": 123}}}}
            """.trimMargin()
        )
    )

    include(
        stringTest(
            name = "data class with serial names",
            dataClass = DataClassWithSerialNames(
                id = ObjectId("64a2a1bcac2cb9126e80d408"),
                name = "Bob",
                string = "def",
            ),
            json = """
                { "_id": {"${'$'}oid":"64a2a1bcac2cb9126e80d408"}, "nom": "Bob", "string": "def" }
            """.trimIndent()
        )
    )

    include(
        stringTest(
            name = "data class with bson values",
            dataClass = DataClassWithBsonValues(
                array = listOf("abc".toBson()).toBson(),
                binary = UUID(123, 456).toBson(),
                boolean = true.toBson(),
                dateTime = Instant.EPOCH.toBson(),
                decimal128 = BigDecimal.ONE.toBson(),
                document = BsonDocument("name", "Bob".toBson()),
                double = 123.0.toBson(),
                int32 = 123.toBson(),
                int64 = 123L.toBson(),
                maxKey = BsonMaxKey(),
                minKey = BsonMinKey(),
                objectId = ObjectId("64a2a1bcac2cb9126e80d408").toBson(),
                string = "abc".toBson(),
                timestamp = BsonTimestamp(123, 4),
                undefined = BsonUndefined(),
            ),
            json = """{
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
        )
    )

    include(
        stringTest(
            name = "data class with transient",
            dataClass = DataClassWithTransient(
                string = "abc",
                transient = "def"
            ),
            json = """
                { "string": "abc" }
            """.trimIndent()
        )
    )

    include(
        stringTest(
            name = "data class with big Long",
            dataClass = DataClassWithSingleValue(
                n = Long.MAX_VALUE
            ),
            json = """
                { "n": 9223372036854775807 }
            """.trimIndent()
        )
    )
    include(
        stringTest(
            name = "data class with small Long",
            dataClass = DataClassWithSingleValue(
                n = 123L
            ),
            json = """
                { "n": 123 }
            """.trimIndent()
        )
    )
    include(
        stringTest(
            name = "data class with encode defaults (encodeDefaults=true)",
            dataClass = DataClassWithEncodeDefault(
                never = "default",
                always = "default",
            ),
            json = """
                { "always": "default" }
            """.trimIndent()
        )
    )
    include(
        stringTest(
            name = "data class with encode defaults (encodeDefaults=false)",
            dataClass = DataClassWithEncodeDefault(
                never = "default",
                always = "default",
            ),
            json = """
                { "always": "default" }
            """.trimIndent(),
            bson = Bson {
                encodeDefaults = false
            }
        )
    )
    include(
        stringTest(
            name = "boxed data class",
            dataClass = Boxed(
                contents = DataClassWithSingleValue(
                    n = 123L
                )
            ),
            json = """
                { "contents": { "n": 123 } }
            """.trimIndent()
        )
    )
    include(
        stringTest(
            name = "boxed bson document",
            dataClass = Boxed(contents = BsonDocument("name", "bob".toBson())),
            json = """
                { "contents": { "name": "bob" } }
            """.trimIndent()
        )
    )
    include(
        stringTest(
            name = "custom boxed data class",
            dataClass = CustomBoxed(definition = BsonDocument("name", "bob".toBson())),
            json = """
                { "name": "bob" }
            """.trimIndent()
        )
    )
    context("unknown keys") {
        val dataClass = DataClassWithSingleValue(
            n = 123L
        )
        val json = """
            { "n": 123, "unknown": "abc" }
        """.trimIndent()

        test("succeeds when ignoreUnknownKeys=true") {
            Bson.decodeFromString<DataClassWithSingleValue>(json) shouldBe dataClass
        }
        test("fails when ignoreUnknownKeys=false)") {
            shouldThrow<SerializationException> {
                Bson.decodeFromString<DataClassWithSingleValue>(json) shouldBe dataClass
            }
        }
    }


})