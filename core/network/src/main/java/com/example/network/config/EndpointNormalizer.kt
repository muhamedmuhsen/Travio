package com.example.network.config

object EndpointNormalizer {
    fun normalizeApiBaseUrl(raw: String): String {
        val withProtocol = ensureProtocol(raw)
        return withProtocol.removeSuffix("/api/").removeSuffix("/api").trimEnd('/') + "/api/"
    }

    fun normalizeImageBaseUrl(raw: String): String {
        val withProtocol = ensureProtocol(raw)
        return withProtocol.removeSuffix("/api/").removeSuffix("/api").trimEnd('/')
    }

    fun isLocalhostHost(url: String): Boolean {
        return url.contains("://localhost") || url.contains("://127.0.0.1")
    }

    fun isEmulatorBridgeHost(url: String): Boolean {
        return url.contains("://10.0.2.2")
    }

    private fun ensureProtocol(raw: String): String {
        val trimmed = raw.trim()
        return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else {
            "http://$trimmed"
        }
    }
}
