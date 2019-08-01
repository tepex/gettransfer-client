package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.HandleUrlPresenter
import com.kg.gettransfer.presentation.view.HandleUrlView
import com.kg.gettransfer.presentation.view.HandleUrlView.Companion.FROM_PLACE_ID
import com.kg.gettransfer.presentation.view.HandleUrlView.Companion.PROMO_CODE
import com.kg.gettransfer.presentation.view.HandleUrlView.Companion.TO_PLACE_ID
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import org.slf4j.Logger


class AppsFlyerHandleDeepLinkActivity : BaseActivity(), HandleUrlView {

    @InjectPresenter
    internal lateinit var presenter: HandleUrlPresenter

    override fun getPresenter(): HandleUrlPresenter = presenter

    @ProvidePresenter
    fun createHandleUrlPresenter() = HandleUrlPresenter()

    private val log: Logger by inject { parametersOf("GTR-presenter") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handle_url)
        AppsFlyerLib.getInstance().sendDeepLinkData(this)
        AppsFlyerLib.getInstance().registerConversionListener(this, object : AppsFlyerConversionListener {

            /* Returns the attribution data. Note - the same conversion data is returned every time per install */
            override fun onInstallConversionDataLoaded(conversionData: Map<String, String>) {
                for (attrName in conversionData.keys) {
                    log.debug(AppsFlyerLib.LOG_TAG, "attribute: " + attrName + " = " + conversionData[attrName])
                }
            }

            override fun onInstallConversionFailure(errorMessage: String) {
                log.debug(AppsFlyerLib.LOG_TAG, "error onInstallConversionFailure : $errorMessage")
            }

            /* Called only when a Deep Link is opened */
            override fun onAppOpenAttribution(conversionData: Map<String, String>) {
                log.debug("Attribution Data:")
                for (attrName in conversionData.keys) {
                    log.debug(AppsFlyerLib.LOG_TAG, "attribute: " + attrName + " = " +
                            conversionData[attrName])
                }

                presenter.createOrder(
                    conversionData[FROM_PLACE_ID],
                    conversionData[TO_PLACE_ID],
                    conversionData[PROMO_CODE]
                )
            }

            override fun onAttributionFailure(errorMessage: String) {
                log.debug(AppsFlyerLib.LOG_TAG, "error onAttributionFailure : $errorMessage")
            }
        })
    }
}