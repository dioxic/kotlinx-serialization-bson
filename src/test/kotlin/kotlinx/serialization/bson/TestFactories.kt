@file:OptIn(ExperimentalSerializationApi::class)

package kotlinx.serialization.bson

import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.*
import org.bson.BsonDocument

inline fun <reified T> stringTest(
    name: String,
    dataClass: T,
    json: String,
    bson: Bson = Bson,
) = funSpec {
    context(name) {
        test("encode") {
            bson.encodeToString(dataClass) shouldBeJson json
        }
        test("decode") {
            bson.decodeFromString<T>(json) shouldBe dataClass
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