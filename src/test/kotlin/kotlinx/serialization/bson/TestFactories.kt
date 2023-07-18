package kotlinx.serialization.bson

import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.bson.internal.decodeFromStream
import kotlinx.serialization.bson.internal.encodeToStream
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.encodeToString
import org.bson.BsonDocument
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

inline fun <reified T> stringTest(
    name: String,
    dataClass: T,
    json: String,
    decodeJson: List<String> = listOf(json),
    bson: Bson = Bson,
) = funSpec {
    context(name) {
        test("encode") {
            bson.encodeToString(dataClass) shouldBeJson json
        }
        test("decode") {
            decodeJson.forEach {
                bson.decodeFromString<T>(it) shouldBe dataClass
            }
        }
    }
}

inline fun <reified T> streamTest(
    name: String,
    dataClass: T,
    json: String,
    decodeJson: List<String> = listOf(json),
    bson: Bson = Bson,
) = funSpec {
    context(name) {
        test("encode") {
            ByteArrayOutputStream().use { stream ->
                bson.encodeToStream(dataClass, stream)
                stream.flush()
                String(stream.toByteArray()) shouldBeJson json
            }
        }
        test("decode") {
            decodeJson.forEach { json ->
                ByteArrayInputStream(json.toByteArray()).use { stream ->
                    bson.decodeFromStream<T>(stream) shouldBe dataClass
                }
            }
        }
    }
}

inline fun <reified T> binaryTest(
    name: String,
    dataClass: T,
    hexString: String,
    bson: Bson = Bson,
) = funSpec {
    context(name) {
        test("encode") {
            bson.encodeToHexString(dataClass) shouldBeJson hexString
        }
        test("decode") {
            bson.decodeFromHexString<T>(hexString) shouldBe dataClass
        }
    }
}

inline fun <reified T> bsonDocumentTest(
    name: String,
    dataClass: T,
    document: BsonDocument,
    bson: Bson = Bson,
) = funSpec {
    context(name) {
        test("encode") {
            bson.encodeToBsonDocument(dataClass) shouldBe document
        }
        test("decode") {
            bson.decodeFromBsonDocument<T>(document) shouldBe dataClass
        }
    }
}