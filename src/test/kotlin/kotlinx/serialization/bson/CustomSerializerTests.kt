package kotlinx.serialization.bson

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.bson.fixtures.CustomSerializer
import kotlinx.serialization.bson.fixtures.DataClassWithSingleValue

class CustomSerializerTests: FunSpec ({

    context("single value") {
        val document = buildBsonDocument {
            put("n", 123L)
        }
        val dc = DataClassWithSingleValue(
            n = 123L
        )

        test("encode") {
            Bson.encodeToBsonDocument(CustomSerializer, dc) shouldBe document
        }
        test("decode") {
            Bson.decodeFromBsonDocument(CustomSerializer, document) shouldBe dc
        }
    }
})