package kotlinx.serialization.bson

import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.bson.fixtures.DataClassWithSerialNames
import org.bson.types.ObjectId

class BinaryBsonTests : FunSpec({

    with(Bson) {
        include(
            binaryTest(
                name = "data class with serial names",
                dataClass = DataClassWithSerialNames(
                    id = ObjectId("64a2a1bcac2cb9126e80d408"),
                    name = "Bob",
                    string = "def",
                ),
                hexString = "33000000075f69640064a2a1bcac2cb9126e80d408026e6f6d0004000000426f620002737472696e6700040000006465660000"
            )
        )
    }

})