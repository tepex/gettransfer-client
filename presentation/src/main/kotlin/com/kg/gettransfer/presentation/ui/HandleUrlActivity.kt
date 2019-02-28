package com.kg.gettransfer.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.presentation.presenter.HandleUrlPresenter
import com.kg.gettransfer.presentation.view.HandleUrlView
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import org.jetbrains.anko.longToast

class HandleUrlActivity : BaseActivity(), HandleUrlView {

    @InjectPresenter
    internal lateinit var presenter: HandleUrlPresenter

    override fun getPresenter(): HandleUrlPresenter = presenter

    @ProvidePresenter
    fun createHandleUrlPresenter() = HandleUrlPresenter()

    companion object {
        const val PASSENGER_CABINET = "/passenger/cabinet"
        const val PASSENGER_RATE = "/passenger/rate"
        const val CARRIER_CABINET = "/carrier/cabinet"
        const val CHOOSE_OFFER_ID = "choose_offer_id"
        const val OPEN_CHAT = "open_chat"
        const val TRANSFERS = "transfers"
        const val SLASH = "/"
        const val EQUAL = "="
        const val QUESTION = "?"
        const val RATE = "rate_val"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handle_url)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val appLinkAction = intent?.action
        val appLinkData : Uri? = intent?.data
        if (Intent.ACTION_VIEW == appLinkAction) {
            val path = appLinkData?.path
            when {
                path.equals(PASSENGER_CABINET) -> appLinkData?.fragment?.let {
                    if (it.startsWith(TRANSFERS)) {
                        if (it.contains(CHOOSE_OFFER_ID)) {
                            val transferId = it.substring(it.indexOf(SLASH) + 1, it.indexOf(QUESTION)).toLong()
                            val offerId = it.substring(it.lastIndexOf(EQUAL) + 1, it.length).toLong()
                            presenter.openOffer(transferId, offerId)
                            return
                        } else if (it.contains(OPEN_CHAT)) {
                            val chatId = it.substring(it.indexOf(SLASH) + 1, it.indexOf(QUESTION))
                            presenter.openChat(chatId)
                            return
                        }
                        val transferId = it.substring(it.indexOf(SLASH) + 1).toLong()
                        presenter.openTransfer(transferId)
                        return
                    }
                }
                path?.startsWith(PASSENGER_RATE)!! -> {
                    val transferId = appLinkData.lastPathSegment?.toLong()
                    val rate = appLinkData.getQueryParameter(RATE)?.toInt()
                    presenter.rateTransfer(transferId!!, rate!!)
                    return
                }
                else -> finish()
            }
        }
    }

    override fun setError(e: ApiException) {
        longToast(e.details)
        finish()
    }
}