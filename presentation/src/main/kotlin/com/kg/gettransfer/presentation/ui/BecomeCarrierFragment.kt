package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import moxy.MvpAppCompatFragment
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.setThrottledClickListener
import com.kg.gettransfer.presentation.listeners.GoToPlayMarketListener
import com.kg.gettransfer.utilities.Analytics
import kotlinx.android.synthetic.main.dialog_fragment_about_driver_app.btn_continue
import kotlinx.android.synthetic.main.toolbar_nav_back.*
import kotlinx.android.synthetic.main.toolbar_nav_back.view.*
import org.koin.android.ext.android.inject

class BecomeCarrierFragment : MvpAppCompatFragment() {
    private val analytics: Analytics by inject()
    private var onAboutDriverAppListener: GoToPlayMarketListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_become_carrier, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        btn_continue.setOnClickListener {
            analytics.logEvent(Analytics.EVENT_BECOME_CARRIER, Analytics.GO_TO_MARKET, null)
            onAboutDriverAppListener?.onClickGoToDriverApp()
            navigateUp()
        }
    }

    private fun setupToolbar() {
        toolbar.ivBack.setThrottledClickListener { navigateUp() }
        toolbar.toolbar_title.text = getString(R.string.LNG_RIDE_CREATE_CARRIER)
    }

    private fun navigateUp() {
        // TODO fix it after support single activity in whole app
        if (activity is MainNavigateActivity) {
            findNavController().navigateUp()
        } else {
            fragmentManager?.popBackStack()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onAboutDriverAppListener = activity as? GoToPlayMarketListener
    }
}
