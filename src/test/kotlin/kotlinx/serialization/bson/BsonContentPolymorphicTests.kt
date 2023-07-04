package kotlinx.serialization.bson

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.bson.fixtures.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.bson.json.JsonMode

class BsonContentPolymorphicTests : FunSpec({

    val bson = Bson {
        encodeDefaults = true
        explicitNulls = true
        jsonMode = JsonMode.RELAXED
    }

    context("root") {
        test("encoding individual has no class discriminator field") {
            with(dataClassIndividual) {
                bson.encodeToString<Party>(dataClass) shouldBeJson expectedJson
            }
        }

        test("encoding organisation has no class discriminator field") {
            with(dataClassOrganisation) {
                bson.encodeToString<Party>(dataClass) shouldBeJson expectedJson
            }
        }

        test("decoding individual works without a class discriminator field") {
            with(dataClassIndividual) {
                bson.decodeFromString<Party>(expectedJson) shouldBe dataClass
            }
        }

        test("decoding organisation works without a class discriminator field") {
            with(dataClassOrganisation) {
                bson.decodeFromString<Party>(expectedJson) shouldBe dataClass
            }
        }
    }

    context("embedded") {
        test("encoding individual has no class discriminator field") {
            with(dataClassWithEmbeddedIndividual) {
                bson.encodeToString(dataClass) shouldBeJson expectedJson
            }
        }

        test("encoding organisation has no class discriminator field") {
            with(dataClassWithEmbeddedOrganisation) {
                bson.encodeToString(dataClass) shouldBeJson expectedJson
            }
        }

        test("decoding individual works without a class discriminator field") {
            with(dataClassWithEmbeddedIndividual) {
                bson.decodeFromString<DataClassWithParty>(expectedJson) shouldBe dataClass
            }
        }

        test("decoding organisation works without a class discriminator field") {
            with(dataClassWithEmbeddedOrganisation) {
                bson.decodeFromString<DataClassWithParty>(expectedJson) shouldBe dataClass
            }
        }
    }


})