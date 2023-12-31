@file:Suppress("FunctionName")

package kotlinx.serialization.bson.internal

import kotlinx.serialization.SerializationException

internal const val ignoreUnknownKeysHint = "Use 'ignoreUnknownKeys = true' in 'Bson {}' builder to ignore unknown keys."

internal fun UnknownKeyException(key: String) = SerializationException(
    "Encountered an unknown key '$key'.\n" +
            ignoreUnknownKeysHint
)