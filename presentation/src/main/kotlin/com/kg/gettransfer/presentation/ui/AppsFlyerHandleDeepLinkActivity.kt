package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import androidx.annotation.CallSuper

import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib

import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.BaseHandleUrlPresenter
import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.utilities.DeeplinkManager.Companion.FROM_PLACE_ID
import com.kg.gettransfer.utilities.DeeplinkManager.Companion.PROMO_CODE
import com.kg.gettransfer.utilities.DeeplinkManager.Companion.TO_PLACE_ID

import timber.log.Timber

class AppsFlyerHandleDeepLinkActivity : BaseActivity(), BaseView {

    @InjectPresenter
    internal lateinit var presenter: BaseHandleUrlPresenter<BaseView>

    override fun getPresenter(): BaseHandleUrlPresenter<BaseView> = presenter

    @ProvidePresenter
    fun createHandleUrlPresenter() = BaseHandleUrlPresenter<BaseView>()

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handle_url)
        AppsFlyerLib.getInstance().sendDeepLinkData(this)
        AppsFlyerLib.getInstance().registerConversionListener(this, object : AppsFlyerConversionListener {

            /* Returns the attribution data. Note - the same conversion data is returned every time per install */
            override fun onConversionDataSuccess(conversionData: MutableMap<String, Any>) {
                for (attrName in conversionData.keys) {
                    Timber.d("attribute: $attrName = ${conversionData[attrName]}")
                }
            }

            override fun onConversionDataFail(errorMessage: String) {
                Timber.d("error onInstallConversionFailure : $errorMessage")
            }

            /* Called only when a Deep Link is opened */
            override fun onAppOpenAttribution(conversionData: Map<String, String>) {
                Timber.d("Attribution Data:")
                for (attrName in conversionData.keys) {
                    Timber.d("attribute: $attrName = ${conversionData[attrName]}")
                }

                presenter.createOrder(
                    conversionData[FROM_PLACE_ID],
                    conversionData[TO_PLACE_ID],
                    conversionData[PROMO_CODE]
                )
            }

            override fun onAttributionFailure(errorMessage: String) {
                Timber.d("error onAttributionFailure : $errorMessage")
            }
        })
    }
}
