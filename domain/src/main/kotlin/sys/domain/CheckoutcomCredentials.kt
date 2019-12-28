package sys.domain

data class CheckoutcomCredentials(
    val environment: String,
    val publicKey: String
) {
    companion object {
        const val ENVIRONMENT_LIVE = "live"
    }
}
