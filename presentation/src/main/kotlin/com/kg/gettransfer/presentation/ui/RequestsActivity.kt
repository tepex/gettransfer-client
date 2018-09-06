package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Gravity
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.presentation.presenter.RequestsPresenter
import com.kg.gettransfer.presentation.view.RequestsView
import kotlinx.android.synthetic.main.activity_requests.*
import kotlinx.android.synthetic.main.toolbar.view.*
import org.koin.android.ext.android.inject

class RequestsActivity: MvpAppCompatActivity(), RequestsView {

    companion object {
        @JvmField val TRANSFERS_ACTIVE   = "Active"
        @JvmField val TRANSFERS_ALL      = "All"
        @JvmField val TRANSFERS_COMLETED = "Completed"
    }

    @InjectPresenter
    internal lateinit var presenter: RequestsPresenter

    private val apiInteractor: ApiInteractor by inject()
    private val coroutineContexts: CoroutineContexts by inject()

    @ProvidePresenter
    fun createRequestsPresenter(): RequestsPresenter = RequestsPresenter(coroutineContexts, apiInteractor)

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
    }

    override fun setRequestsFragments(transfersActive: ArrayList<Transfer>, transfersAll: ArrayList<Transfer>,
                                      transfersCompleted: ArrayList<Transfer>, distanceUnit: String){
        val requestsVPAdapter = RequestsViewPagerAdapter(supportFragmentManager)

        val fragmentRequestsActive = RequestsFragment.newInstance(transfersActive, distanceUnit, TRANSFERS_ACTIVE)
        requestsVPAdapter.addFragment(fragmentRequestsActive, TRANSFERS_ACTIVE)
        val fragmentRequestsAll = RequestsFragment.newInstance(transfersAll, distanceUnit, TRANSFERS_ALL)
        requestsVPAdapter.addFragment(fragmentRequestsAll, TRANSFERS_ALL)
        val fragmentRequestsCompleted = RequestsFragment.newInstance(transfersCompleted, distanceUnit, TRANSFERS_COMLETED)
        requestsVPAdapter.addFragment(fragmentRequestsCompleted, TRANSFERS_COMLETED)

        vpRequests.adapter = requestsVPAdapter
        tabs.setupWithViewPager(vpRequests)
    }

    private class RequestsViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
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