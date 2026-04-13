package com.example.travio.config

object ConfigSourcePrecedenceResolver {
    fun resolve(
        ciValue: String?,
        secureValue: String?,
        localValue: String?,
        fallbackValue: String
    ): String {
        return ciValue?.takeIf { it.isNotBlank() }
            ?: secureValue?.takeIf { it.isNotBlank() }
            ?: localValue?.takeIf { it.isNotBlank() }
            ?: fallbackValue
    }
}
