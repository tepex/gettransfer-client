package com.kg.gettransfer.presentation.ui.helpers

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.sys.domain.BuildsConfigs

object BuildsConfigsHelper {
    const val SETTINGS_FIELD_UPDATE_REQUIRED = "settings_field_update_required"
    private const val OPERATOR_LESS   = "<"
    private const val OPERATOR_EQUALS = "="
    private const val OPERATOR_MORE   = ">"

    fun getConfigsForCurrentBuildByField(field: String, buildsConfigs: Map<String, BuildsConfigs>): BuildsConfigs? {
        val appVersion = BuildConfig.VERSION_CODE
        var configsForCurrentBuild: BuildsConfigs? = null
        listOf(OPERATOR_LESS, OPERATOR_EQUALS, OPERATOR_MORE).forEach { operator ->
            var configForVersion: Int? = null
            filterVersionConfigsByPriority(operator, appVersion, buildsConfigs).forEach {
                val version: Int
                if ( when(operator) {
                            OPERATOR_LESS -> {
                                version = it.key.substring(1).toInt()
                                appVersion < version
                            }
                            OPERATOR_EQUALS -> {
                                version = it.key.toInt()
                                appVersion == version
                            }
                            OPERATOR_MORE -> {
                                version = it.key.substring(1).toInt()
                                appVersion > version
                            }
                            else -> throw UnsupportedOperationException()
                        }
                ) {
                    val searchedFieldIsNotNull = when(field) {
                        SETTINGS_FIELD_UPDATE_REQUIRED -> it.value.updateRequired != null
                        else -> throw UnsupportedOperationException()
                    }
                    if ((configForVersion == null || configForVersion!! < version) && searchedFieldIsNotNull ) {
                        configForVersion = version
                        configsForCurrentBuild = it.value
                    }
                }
            }
            if (configsForCurrentBuild != null) return configsForCurrentBuild
        }
        return null
    }

    private fun filterVersionConfigsByPriority(
        operator: String,
        appVersion: Int,
        buildsConfigs: Map<String, BuildsConfigs>
    ) = buildsConfigs.entries.filter {
        if (operator != OPERATOR_EQUALS) it.key.indexOf(operator) == 0
        else {
            val firstSymbol = it.key.substring(0, 1)
            firstSymbol != OPERATOR_LESS && firstSymbol != OPERATOR_MORE && it.key.toInt() == appVersion
        }
    }
}
