package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager

import android.support.v7.widget.Toolbar

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

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
    
    companion object {
        @JvmField val CATEGORY_ACTIVE    = "Active"
        @JvmField val CATEGORY_ALL       = "All"
        @JvmField val CATEGORY_COMPLETED = "Completed"
    }

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_requests)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        (toolbar as Toolbar).toolbar_title.setText(R.string.LNG_MENU_TITLE_RIDES)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }

        val requestsVPAdapter = RequestsViewPagerAdapter(supportFragmentManager)

        viewNetworkNotAvailable = textNetworkNotAvailable

        val fragmentRequestsActive = RequestsFragment.newInstance(CATEGORY_ACTIVE)
        requestsVPAdapter.addFragment(fragmentRequestsActive, CATEGORY_ACTIVE)
        val fragmentRequestsAll = RequestsFragment.newInstance(CATEGORY_ALL)
        requestsVPAdapter.addFragment(fragmentRequestsAll, CATEGORY_ALL)
        val fragmentRequestsCompleted = RequestsFragment.newInstance(CATEGORY_COMPLETED)
        requestsVPAdapter.addFragment(fragmentRequestsCompleted, CATEGORY_COMPLETED)

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
                    0 -> sendEventLog(CATEGORY_ACTIVE)
                    1 -> sendEventLog(CATEGORY_ALL)
                    2 -> sendEventLog(CATEGORY_COMPLETED)
                }
            }
        })
    }
}
