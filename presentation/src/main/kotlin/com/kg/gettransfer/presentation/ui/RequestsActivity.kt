package com.kg.gettransfer.presentation.ui

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
        //toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorYellow))

        val requestsVPAdapter = RequestsViewPagerAdapter(supportFragmentManager)

        viewNetworkNotAvailable = textNetworkNotAvailable

        val fragmentRequestsActive = RequestsFragment.newInstance(RequestsView.CATEGORY_ACTIVE)
        requestsVPAdapter.addFragment(fragmentRequestsActive, getString(R.string.LNG_RIDES_ACTIVE))
        val fragmentRequestsAll = RequestsFragment.newInstance(RequestsView.CATEGORY_ALL)
        requestsVPAdapter.addFragment(fragmentRequestsAll, getString(R.string.LNG_RIDES_ALL))
        val fragmentRequestsCompleted = RequestsFragment.newInstance(RequestsView.CATEGORY_COMPLETED)
        requestsVPAdapter.addFragment(fragmentRequestsCompleted, getString(R.string.LNG_RIDES_COMPLETED))

        vpRequests.adapter = requestsVPAdapter
        tabs.setupWithViewPager(vpRequests)
        setListenersForLog()
    }

    private class RequestsViewPagerAdapter(manager: FragmentManager): FragmentPagerAdapter(manager) {
        val fragments = mutableListOf<Fragment>()
        val titles = mutableListOf<String>()

        override fun getItem(position: Int) = fragments.get(position)
        override fun getCount() = fragments.size
        override fun getPageTitle(position: Int) = titles.get(position)

        fun addFragment(fragment: Fragment, title: String) {
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
                    0 -> sendEventLog(RequestsView.CATEGORY_ACTIVE)
                    1 -> sendEventLog(RequestsView.CATEGORY_ALL)
                    2 -> sendEventLog(RequestsView.CATEGORY_COMPLETED)
                }
            }
        })
    }

    override fun showOffer(offer: OfferModel) {
    }
}
