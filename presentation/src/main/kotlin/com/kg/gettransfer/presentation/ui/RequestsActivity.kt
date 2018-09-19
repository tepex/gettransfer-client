package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import android.support.v7.widget.Toolbar

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.presenter.RequestsPresenter
import com.kg.gettransfer.presentation.view.RequestsView

import kotlinx.android.synthetic.main.activity_requests.*
import kotlinx.android.synthetic.main.toolbar.view.*

import org.koin.android.ext.android.inject

class RequestsActivity: BaseActivity(), RequestsView {

    @InjectPresenter
    internal lateinit var presenter: RequestsPresenter
    
    @ProvidePresenter
    fun createRequestsPresenter() = RequestsPresenter(coroutineContexts, router, systemInteractor)

    protected override var navigator = BaseNavigator(this)

    override fun getPresenter(): RequestsPresenter = presenter
    
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
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).toolbar_title.setText(R.string.text_nav_requests_title)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }

        val requestsVPAdapter = RequestsViewPagerAdapter(supportFragmentManager)

        val fragmentRequestsActive = RequestsFragment.newInstance(CATEGORY_ACTIVE)
        requestsVPAdapter.addFragment(fragmentRequestsActive, CATEGORY_ACTIVE)
        val fragmentRequestsAll = RequestsFragment.newInstance(CATEGORY_ALL)
        requestsVPAdapter.addFragment(fragmentRequestsAll, CATEGORY_ALL)
        val fragmentRequestsCompleted = RequestsFragment.newInstance(CATEGORY_COMPLETED)
        requestsVPAdapter.addFragment(fragmentRequestsCompleted, CATEGORY_COMPLETED)

        vpRequests.adapter = requestsVPAdapter
        tabs.setupWithViewPager(vpRequests)
    }

    private class RequestsViewPagerAdapter(manager: FragmentManager): FragmentPagerAdapter(manager) {
        val fragments = arrayListOf<Fragment>()
        val titles = arrayListOf<String>()
        
        override fun getItem(position: Int): Fragment = fragments.get(position)

        override fun getCount(): Int = fragments.size

        override fun getPageTitle(position: Int): CharSequence? = titles.get(position)

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }
    }
}
