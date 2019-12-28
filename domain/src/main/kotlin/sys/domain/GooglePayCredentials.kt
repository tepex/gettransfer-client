package sys.domain

data class GooglePayCredentials(
    val environment: ENVIRONMENT,
    val merchantId: String,
    val merchantName: String,
    val cardNetworks: List<String>,
    val authMethods: List<String>
) {

    enum class ENVIRONMENT {
        PRODUCTION, TEST, UNKNOWN
    }
}
