package com.example.feature.chat.data.mapper

import com.example.feature.chat.domain.model.AiGenerationError
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException

object AiErrorMapper {

    private val CONNECTION_REFUSED_PATTERN =
        Regex("target machine actively refused it|connection refused", RegexOption.IGNORE_CASE)

    private val TIMEOUT_PATTERN =
        Regex("timed?\\s*out|timeout|deadline exceeded", RegexOption.IGNORE_CASE)

    private val UNAVAILABLE_PATTERN =
        Regex("service unavailable|unavailable|503|server unavailable", RegexOption.IGNORE_CASE)

    private val IP_PORT_PATTERN =
        Regex("\\b\\d{1,3}(\\.\\d{1,3}){3}(:\\d+)?\\b")

    private val STACK_TRACE_PATTERN =
        Regex("\\s+at\\s+[\\w.$]+\\([^)]*\\)")

    fun classify(
        rawError: String?,
        httpCode: Int? = null
    ): AiGenerationError {
        Timber.e("AI Error [raw]: %s (httpCode=%s)", rawError, httpCode)

        if (rawError == "sync_trip_id_failed") {
            return AiGenerationError.SyncTripIdFailed
        }

        val sanitized = rawError?.let { sanitize(it) }.orEmpty()

        return when {
            rawError != null && CONNECTION_REFUSED_PATTERN.containsMatchIn(rawError) ->
                AiGenerationError.AiConnectionRefused

            rawError != null && TIMEOUT_PATTERN.containsMatchIn(rawError) ->
                AiGenerationError.AiTimeout

            rawError != null && UNAVAILABLE_PATTERN.containsMatchIn(rawError) ->
                AiGenerationError.AiServiceUnavailable

            httpCode == 500 || httpCode == 502 || httpCode == 503 ->
                AiGenerationError.AiServiceUnavailable

            else -> AiGenerationError.Unknown(sanitized)
        }
    }

    fun classifyException(exception: Exception): AiGenerationError {
        Timber.e(exception, "AI Error [exception]")

        return when {
            exception is ConnectException -> AiGenerationError.AiConnectionRefused
            exception is SocketTimeoutException -> AiGenerationError.AiTimeout
            else -> {
                val httpCode = extractHttpCode(exception)
                val responseBody = extractResponseBody(exception)

                if (httpCode != null || responseBody != null) {
                    classify(rawError = responseBody ?: exception.message, httpCode = httpCode)
                } else {
                    AiGenerationError.Unknown(sanitize(exception.message.orEmpty()))
                }
            }
        }
    }

    fun sanitize(raw: String): String {
        return raw
            .replace(IP_PORT_PATTERN, "[redacted]")
            .replace(STACK_TRACE_PATTERN, "")
            .trim()
    }

    private fun extractResponseBody(exception: Exception): String? {
        return try {
            val className = exception::class.qualifiedName.orEmpty()
            if (className.contains("HttpException") || className.contains("retrofit2")) {
                val responseMethod = exception::class.java.getMethod("response")
                val response = responseMethod.invoke(exception) ?: return null
                val errorBodyMethod = response::class.java.getMethod("errorBody")
                val errorBody = errorBodyMethod.invoke(response) as? okhttp3.ResponseBody
                errorBody?.string()
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.w(e, "Failed to extract error body")
            null
        }
    }

    fun extractHttpCode(exception: Exception): Int? {
        val className = exception::class.qualifiedName.orEmpty()
        if (className.contains("HttpException") || className.contains("retrofit2")) {
            return try {
                val codeMethod = exception::class.java.getMethod("code")
                codeMethod.invoke(exception) as? Int
            } catch (_: Exception) {
                null
            }
        }
        return null
    }
}
