package kotlinx.serialization.bson

import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.bson.fixtures.DataClassWithBsonValues
import kotlinx.serialization.bson.fixtures.DataClassWithSerialNames
import kotlinx.serialization.bson.fixtures.DataClassWithSimpleValues
import org.bson.*
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.Instant
import java.util.*

class BsonDocumentTests : FunSpec({

    include(
        bsonDocumentTest(
            name = "data class with simples values",
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
            document = buildBsonDocument {
                put("boolean", false)
                put("byte", 123)
                put("char", "A")
                put("double", 123.0)
                put("float", 123f)
                put("int", 123)
                put("long", 123L)
                put("short", 123)
                put("string", "abc")
            }
        )
    )
    include(
        bsonDocumentTest(
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
            document = buildBsonDocument {
                putBsonArray("array") {
                    add("abc")
                }
                put("binary", UUID(123, 456))
                put("boolean", true)
                put("dateTime", Instant.EPOCH)
                put("decimal128", BigDecimal.ONE)
                putBsonDocument("document") {
                    put("name", "Bob")
                }
                put("double", 123.0)
                put("int32", 123)
                put("int64", 123L)
                put("maxKey", BsonMaxKey())
                put("minKey", BsonMinKey())
                put("objectId", ObjectId("64a2a1bcac2cb9126e80d408"))
                put("string", "abc")
                put("timestamp", BsonTimestamp(123, 4))
                put("undefined", BsonUndefined())
            }
        )
    )
    include(bsonDocumentTest(
        name = "data class with serial names",
        dataClass = DataClassWithSerialNames(
            id = ObjectId("64a2a1bcac2cb9126e80d408"),
            name = "Bob",
            string = "def",
        ),
        document = buildBsonDocument {
            put("_id", ObjectId("64a2a1bcac2cb9126e80d408"))
            put("nom", "Bob")
            put("string", "def")
        }
    ))

})