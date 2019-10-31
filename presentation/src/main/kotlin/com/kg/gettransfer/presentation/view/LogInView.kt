package com.kg.gettransfer.presentation.view

import kotlinx.serialization.Serializable

interface LogInView : OpenDeepLinkScreenView {
    fun setEmail(login: String)
    fun showValidationError(errorType: Int, phoneExample: String)

    companion object {
        val EXTRA_PARAMS = "${LogInView::class.java.name}.params"
    }

    @Serializable
    data class Params(
        val nextScreen: String,
        val transferId: Long = 0L,
        val offerId: Long = 0L,
        val bookNowTransportId: String = "",
        val rate: Int = 0,
        var emailOrPhone: String = ""
    )
}
