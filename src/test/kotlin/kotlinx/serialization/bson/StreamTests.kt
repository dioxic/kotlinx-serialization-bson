package kotlinx.serialization.bson

import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.bson.fixtures.DataClassWithSimpleValues

class StreamTests : FunSpec({

    include(
        streamTest(
            name = "data class with simple values",
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
            json = """
                { "boolean": false, "byte": 123, "char": "A", "double": 123.0, "float": 123.0, "int": 123, "long": 123, "short": 123, "string": "abc" }
            """.trimIndent()
        )
    )

})

