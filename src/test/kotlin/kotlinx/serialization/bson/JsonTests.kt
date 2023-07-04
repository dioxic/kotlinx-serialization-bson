package kotlinx.serialization.bson

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.bson.fixtures.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.bson.json.JsonMode

class JsonTests : FunSpec({

    val bson = Bson {
        encodeDefaults = true
        explicitNulls = true
        jsonMode = JsonMode.RELAXED
    }
    val bsonNoDefaults = Bson {
        encodeDefaults = false
        explicitNulls = true
        jsonMode = JsonMode.RELAXED
    }

    context("encode to string") {

        test("data class with simple values") {
            with(dataClassWithSimpleValues) {
                bson.encodeToString(dataClass) shouldBeJson expectedJson
            }
        }

        test("data class with collections") {
            with(dataClassWithCollections) {
                bson.encodeToString(dataClass) shouldBeJson expectedJson
            }
        }

        test("data class with nulls") {
            with(dataClassWithNulls) {
                bson.encodeToString(dataClass) shouldBeJson expectedJson
            }
        }

        test("data class with embedded") {
            with(dataClassWithEmbedded) {
                bson.encodeToString(dataClass) shouldBeJson expectedJson
            }
        }

        test("data class with serial names") {
            with(dataClassWithSerialNames) {
                bson.encodeToString(dataClass) shouldBeJson expectedJson
            }
        }

        test("data class with encode defaults (encodeDefaults=true)") {
            with(dataClassWithEncodeDefault) {
                bson.encodeToString(dataClass) shouldBeJson expectedJson
            }
        }

        test("data class with encode defaults (encodeDefaults=false)") {
            with(dataClassWithEncodeDefault) {
                bsonNoDefaults.encodeToString(dataClass) shouldBeJson expectedJson
            }
        }

        test("data class with bson values (small Long") {
            with(dataClassWithBsonValuesSmallLong) {
                bson.encodeToString(dataClass) shouldBeJson expectedJson
            }
        }

        test("data class with bson values (big Long)") {
            with(dataClassWithBsonValuesBigLong) {
                bson.encodeToString(dataClass) shouldBeJson expectedJson
            }
        }

        test("data class with transient") {
            with(dataClassWithTransient) {
                bson.encodeToString(dataClass) shouldBeJson expectedJson
            }
        }
    }

    context("decode from string") {

        test("data class with simple values") {
            with(dataClassWithSimpleValues) {
                bson.decodeFromString<DataClassWithSimpleValues>(expectedJson) shouldBe dataClass
            }
        }

        test("data class with collections") {
            with(dataClassWithCollections) {
                bson.decodeFromString<DataClassWithCollections>(expectedJson) shouldBe dataClass
            }
        }

        test("data class with nulls") {
            with(dataClassWithNulls) {
                bson.decodeFromString<DataClassWithNulls>(expectedJson) shouldBe dataClass
            }
        }

        test("data class with embedded") {
            with(dataClassWithEmbedded) {
                bson.decodeFromString<DataClassWithEmbedded>(expectedJson) shouldBe dataClass
            }
        }

        test("data class with serial names") {
            with(dataClassWithSerialNames) {
                bson.decodeFromString<DataClassWithSerialNames>(expectedJson) shouldBe dataClass
            }
        }

        test("data class with encode defaults (encodeDefaults=true)") {
            with(dataClassWithEncodeDefault) {
                bson.decodeFromString<DataClassWithEncodeDefault>(expectedJson) shouldBe dataClass
            }
        }

        test("data class with encode defaults (encodeDefaults=false)") {
            with(dataClassWithEncodeDefault)
            { bsonNoDefaults.decodeFromString<DataClassWithEncodeDefault>(expectedJson) shouldBe dataClass }
        }

        test("data class with bson values (small Long)") {
            with(dataClassWithBsonValuesSmallLong) {
                bson.decodeFromString<DataClassWithBsonValues>(expectedJson) shouldBe dataClass
            }
        }

        test("data class with bson values (big Long)") {
            with(dataClassWithBsonValuesBigLong) {
                bson.decodeFromString<DataClassWithBsonValues>(expectedJson) shouldBe dataClass
            }
        }

        test("data class with transient") {
            with(dataClassWithTransient) {
                bson.decodeFromString<DataClassWithTransient>(expectedJson) shouldBe dataClass
            }
        }
    }

})