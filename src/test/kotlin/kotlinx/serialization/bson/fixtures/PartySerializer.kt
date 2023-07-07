package kotlinx.serialization.bson.fixtures

import kotlinx.serialization.bson.BsonContentPolymorphicSerializer
import org.bson.BsonDocument

object PartySerializer : BsonContentPolymorphicSerializer<Party>(Party::class) {
        override fun selectDeserializer(document: BsonDocument) = when {
            "height" in document -> Individual.serializer()
            "companyId" in document -> Organisation.serializer()
            else -> error("No valid serializer")
        }
    }