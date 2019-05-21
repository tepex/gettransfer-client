package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager

import android.support.v7.widget.Toolbar

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.presenter.RequestsPresenter
import com.kg.gettransfer.presentation.view.RequestsView
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ACTIVE
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ARCHIVE
import kotlinx.android.synthetic.main.activity_requests.*

class RequestsActivity: BaseActivity(), RequestsView {

    @InjectPresenter
    internal lateinit var presenter: RequestsPresenter

    @ProvidePresenter
    fun createRequestsPresenter() = RequestsPresenter()

    override fun getPresenter(): RequestsPresenter = presenter
    private fun sendEventLog(param: String) = presenter.logEvent(param)
    private var requestsVPAdapter: RequestsViewPagerAdapter?  = null

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_requests)

        setToolbar(toolbar as Toolbar, R.string.LNG_MENU_TITLE_RIDES)

        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.ic_home_back_24dp)
        }

        requestsVPAdapter = RequestsViewPagerAdapter(supportFragmentManager)

        viewNetworkNotAvailable = textNetworkNotAvailable

        val fragmentRequestsActive = RequestsFragment.newInstance(TRANSFER_ACTIVE)
        requestsVPAdapter?.addFragment(fragmentRequestsActive, getString(R.string.LNG_RIDES_ACTIVE))
        val fragmentRequestsAll = RequestsFragment.newInstance(TRANSFER_ARCHIVE)
        requestsVPAdapter?.addFragment(fragmentRequestsAll, getString(R.string.LNG_RIDES_COMPLETED))

        vpRequests.adapter = requestsVPAdapter
        tabs.setupWithViewPager(vpRequests)
        setListenersForLog()
    }

    private class RequestsViewPagerAdapter(manager: FragmentManager): FragmentPagerAdapter(manager) {
        val fragments = mutableListOf<RequestsFragment>()
        val titles = mutableListOf<String>()

        override fun getItem(position: Int) = fragments[position]
        override fun getCount() = fragments.size
        override fun getPageTitle(position: Int) = titles[position]

        fun addFragment(fragment: RequestsFragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }
    }

    private fun setListenersForLog() {
        vpRequests.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
            override fun onPageSelected(p0: Int) {
                when(p0) {
                    TRANSFER_ACTIVE -> sendEventLog(getString(R.string.LNG_RIDES_ACTIVE))
                    TRANSFER_ARCHIVE ->  { sendEventLog(getString(R.string.LNG_RIDES_COMPLETED)) }
                }
            }
        })
    }
}
