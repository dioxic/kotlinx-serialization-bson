package kotlinx.serialization.bson

import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.bson.fixtures.TestDataClass
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.encodeToString

context(Bson)
inline fun <reified T : TestDataClass> jsonTest(
    name: String,
    dataClass: T,
    json: String
) = funSpec {
    context(name) {
        test("encode") {
            encodeToString(dataClass) shouldBeJson json
        }
        test("decode") {
            decodeFromString<T>(json) shouldBe dataClass
        }
    }
}

context(Bson)
inline fun <reified T : TestDataClass> binaryTest(
    name: String,
    dataClass: T,
    hexString: String
) = funSpec {
    context(name) {
        test("encode") {
            encodeToHexString(dataClass) shouldBeJson hexString
        }
        test("decode") {
            decodeFromHexString<T>(hexString) shouldBe dataClass
        }
    }
}