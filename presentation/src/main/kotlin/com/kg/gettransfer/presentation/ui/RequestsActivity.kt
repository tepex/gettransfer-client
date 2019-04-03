package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager

import android.support.v7.widget.Toolbar

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.presentation.model.OfferModel

import com.kg.gettransfer.presentation.presenter.RequestsPresenter
import com.kg.gettransfer.presentation.view.RequestsView
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ACTIVE
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ARCHIVE

import kotlinx.android.synthetic.main.activity_requests.*
import kotlinx.android.synthetic.main.toolbar.view.*

class RequestsActivity: BaseActivity(), RequestsView {

    @InjectPresenter
    internal lateinit var presenter: RequestsPresenter

    @ProvidePresenter
    fun createRequestsPresenter() = RequestsPresenter()

    override fun getPresenter(): RequestsPresenter = presenter
    private fun sendEventLog(param: String) = presenter.logEvent(param)

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_requests)

        setToolbar(toolbar as Toolbar, R.string.LNG_MENU_TITLE_RIDES)

        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.ic_home_back_24dp)
        }

        val requestsVPAdapter = RequestsViewPagerAdapter(supportFragmentManager, this)

        viewNetworkNotAvailable = textNetworkNotAvailable

        vpRequests.adapter = requestsVPAdapter
        tabs.setupWithViewPager(vpRequests)
        setListenersForLog()
    }

    private class RequestsViewPagerAdapter(manager: FragmentManager,
                                           ctx: Context): FragmentPagerAdapter(manager) {

        private val context = ctx

        override fun getItem(position: Int): Fragment {
            when(position) {
                TRANSFER_ACTIVE -> return RequestsFragment.newInstance(TRANSFER_ACTIVE)
                TRANSFER_ARCHIVE -> return RequestsFragment.newInstance(TRANSFER_ARCHIVE)
                else -> return RequestsFragment.newInstance(TRANSFER_ACTIVE)
            }
        }

        override fun getCount() = 2
        override fun getPageTitle(position: Int): CharSequence? {
            when(position) {
                TRANSFER_ACTIVE -> return context.getString(R.string.LNG_RIDES_ACTIVE)
                TRANSFER_ARCHIVE -> return context.getString(R.string.LNG_RIDES_COMPLETED)
                else -> return context.getString(R.string.LNG_RIDES_ACTIVE)
            }
        }
    }

    private fun setListenersForLog() {
        vpRequests.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
            override fun onPageSelected(p0: Int) {
                when(p0) {
                    TRANSFER_ACTIVE -> sendEventLog(getString(R.string.LNG_RIDES_ACTIVE))
                    TRANSFER_ARCHIVE -> sendEventLog(getString(R.string.LNG_RIDES_COMPLETED))
                }
            }
        })
    }
}
