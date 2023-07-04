package kotlinx.serialization.bson

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.bson.fixtures.DataClassWithSerialNames
import kotlinx.serialization.bson.fixtures.dataClassWithSerialNames
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToHexString

class BinaryBsonTests : FunSpec({

    val bson = Bson {
        encodeDefaults = true
        explicitNulls = true
    }

    test("encode data class with serial names") {
        with(dataClassWithSerialNames) {
            bson.encodeToHexString(dataClass) shouldBeJson expectedBinaryHex
        }
    }

    test("decode data class with serial names") {
        with(dataClassWithSerialNames) {
            bson.decodeFromHexString<DataClassWithSerialNames>(expectedBinaryHex!!) shouldBe dataClass
        }
    }

})