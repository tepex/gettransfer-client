package sys.domain

data class GooglePayCredentials(
    val environment: String,
    val merchantId: String,
    val merchantName: String,
    val cardNetworks: List<String>,
    val authMethods: List<String>
)