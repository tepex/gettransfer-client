package com.kg.gettransfer.sys.domain

class IsNeedUpdateAppInteractor(
    private val getMobileConfigs: GetMobileConfigsInteractor
) {

    /* TODO Какая-то черная магия! Нужно отрефакторить.*/
    @Suppress("ComplexMethod", "NestedBlockDepth")
    suspend operator fun invoke(field: String, appVersion: Int): Boolean {
        val buildsConfigs = getMobileConfigs().getModel().buildsConfigs
        var configsForCurrentBuild: BuildsConfigs? = null

        listOf(OPERATOR_LESS, OPERATOR_EQUALS, OPERATOR_MORE).forEach { op ->
            var configForVersion: Int? = null
            filterVersionConfigsByPriority(op, appVersion, buildsConfigs).forEach { entry ->
                val version: Int
                if (
                    when (op) {
                        OPERATOR_LESS -> {
                            version = entry.key.substring(1).toInt()
                            appVersion < version
                        }
                        OPERATOR_EQUALS -> {
                            version = entry.key.toInt()
                            appVersion == version
                        }
                        OPERATOR_MORE -> {
                            version = entry.key.substring(1).toInt()
                            appVersion > version
                        }
                        else -> throw UnsupportedOperationException()
                    }
                ) {
                    val searchedFieldIsNotNull = when (field) {
                        FIELD_UPDATE_REQUIRED -> entry.value.updateRequired != null
                        else                  -> throw UnsupportedOperationException()
                    }
                    @Suppress("UnsafeCallOnNullableType")
                    if ((configForVersion == null || configForVersion!! < version) && searchedFieldIsNotNull) {
                        configForVersion = version
                        configsForCurrentBuild = entry.value
                    }
                }
            }
            configsForCurrentBuild?.let { return it.updateRequired }
        }
        return false
    }

    private fun filterVersionConfigsByPriority(op: String, appVersion: Int, buildsConfigs: Map<String, BuildsConfigs>) =
        buildsConfigs.entries.filter { entry ->
            if (op != OPERATOR_EQUALS) {
                entry.key.indexOf(op) == 0
            } else {
                val firstSymbol = entry.key.substring(0, 1)
                firstSymbol != OPERATOR_LESS && firstSymbol != OPERATOR_MORE && entry.key.toInt() == appVersion
            }
        }

    companion object {
        const val FIELD_UPDATE_REQUIRED = "field_update_required"
        private const val OPERATOR_LESS   = "<"
        private const val OPERATOR_EQUALS = "="
        private const val OPERATOR_MORE   = ">"
    }
}
