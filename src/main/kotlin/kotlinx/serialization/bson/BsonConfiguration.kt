package kotlinx.serialization.bson

import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings

data class BsonConfiguration internal constructor(
    val encodeDefaults: Boolean = false,
    val ignoreUnknownKeys: Boolean = false,
    val jsonMode: JsonMode = JsonMode.RELAXED,
    val newLineCharacters: String = "\n",
    val prettyPrint: Boolean = false,
    val explicitNulls: Boolean = true,
    val prettyPrintIndent: String = "  ",
    val classDiscriminator: String = "type",
) {
    fun toJsonWriterSettings(): JsonWriterSettings =
        JsonWriterSettings.builder()
            .indent(prettyPrint)
            .indentCharacters(prettyPrintIndent)
            .outputMode(jsonMode)
            .newLineCharacters(newLineCharacters)
            .build()
}