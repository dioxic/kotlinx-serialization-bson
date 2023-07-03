package kotlinx.serialization.bson

import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.bson.fixtures.*
import kotlinx.serialization.encodeToString

class EncodingTests : FunSpec({

    val bson = Bson {
        encodeDefaults = true
        explicitNulls = true
    }
    val bsonNoDefaults = Bson {
        encodeDefaults = false
        explicitNulls = true
    }

//    setOf(
//        dataClassWithSimpleValues,
//        dataClassWithCollections,
//        dataClassWithBsonValues,
//        dataClassWithEmbedded,
//        dataClassWithNulls,
//        dataClassWithEncodeDefault,
//        dataClassWithSerialNames,
//        dataClassWithTransient
//    ).forEach {
//        test(it.dataClass::class.simpleName!!) {
//            bson.encodeToString(it.serializer, it.dataClass) shouldBeJson it.expectedJson
//        }
//    }

    test("data class with simple values") {
        val fixture = dataClassWithSimpleValues
        bson.encodeToString(fixture.dataClass) shouldBeJson fixture.expectedJson
    }

    test("data class with collections") {
        val fixture = dataClassWithCollections
        bson.encodeToString(fixture.dataClass) shouldBeJson fixture.expectedJson
    }

    test("data class with nulls") {
        val fixture = dataClassWithNulls
        bson.encodeToString(fixture.dataClass) shouldBeJson fixture.expectedJson
    }

    test("data class with embedded") {
        val fixture = dataClassWithEmbedded
        bson.encodeToString(fixture.dataClass) shouldBeJson fixture.expectedJson
    }

    test("data class with serial names") {
        val fixture = dataClassWithSerialNames
        bson.encodeToString(fixture.dataClass) shouldBeJson fixture.expectedJson
    }

    test("data class with encode defaults (encodeDefaults=true)") {
        val fixture = dataClassWithEncodeDefault
        bson.encodeToString(fixture.dataClass) shouldBeJson fixture.expectedJson
    }

    test("data class with encode defaults (encodeDefaults=false)") {
        val fixture = dataClassWithEncodeDefault
        bsonNoDefaults.encodeToString(fixture.dataClass) shouldBeJson fixture.expectedJson
    }

    test("data class with bson values") {
        val fixture = dataClassWithBsonValues
        bson.encodeToString(fixture.dataClass) shouldBeJson fixture.expectedJson
    }

    test("data class with transient") {
        val fixture = dataClassWithTransient
        bson.encodeToString(fixture.dataClass) shouldBeJson fixture.expectedJson
    }

})