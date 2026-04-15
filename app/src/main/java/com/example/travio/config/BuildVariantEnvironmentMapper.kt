package com.example.travio.config

object BuildVariantEnvironmentMapper {
    fun environmentForFlavor(flavor: String): EnvironmentName {
        return when {
            flavor.contains("emulator", ignoreCase = true) -> EnvironmentName.EMULATOR
            flavor.contains("devicetester", ignoreCase = true) -> EnvironmentName.TESTER_DEVICE
            flavor.contains("production", ignoreCase = true) -> EnvironmentName.PRODUCTION
            else -> error("Unsupported flavor '$flavor' for environment mapping")
        }
    }
}
