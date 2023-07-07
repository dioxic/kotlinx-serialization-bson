package kotlinx.serialization.bson

import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.bson.fixtures.DataClassWithParty
import kotlinx.serialization.bson.fixtures.Individual
import kotlinx.serialization.bson.fixtures.Organisation

class BsonContentPolymorphicTests : FunSpec({

    include(
        stringTest(
            name = "individual",
            dataClass = Individual(
                name = "Bob",
                height = 26
            ),
            json = """
                { "name": "Bob", "height": 26 }
            """.trimIndent()
        )
    )
    include(
        stringTest(
            name = "organisation",
            dataClass = Organisation(
                name = "MongoDB",
                companyId = 1313
            ),
            json = """
                { "name": "MongoDB", "companyId": 1313 }
            """.trimIndent()
        )
    )
    include(
        stringTest(
            name = "embedded organisation",
            dataClass = DataClassWithParty(
                party = Organisation(
                    name = "MongoDB",
                    companyId = 1313
                )
            ),
            json = """
                { "party": { "name": "MongoDB", "companyId": 1313 } }
            """.trimIndent()
        )
    )
    include(
        stringTest(
            name = "embedded individual",
            dataClass = DataClassWithParty(
                party = Individual(
                    name = "Bob",
                    height = 26
                )
            ),
            json = """
                { "party": { "name": "Bob", "height": 26 } }
            """.trimIndent()
        )
    )
})