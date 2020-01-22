package com.kg.gettransfer.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.annotation.CallSuper

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager

import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.presenter.RequestsPresenter
import com.kg.gettransfer.presentation.view.BaseNetworkWarning
import com.kg.gettransfer.presentation.view.RequestsView
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ACTIVE
import com.kg.gettransfer.presentation.view.RequestsView.TransferTypeAnnotation.Companion.TRANSFER_ARCHIVE
import com.kg.gettransfer.utilities.NetworkLifeCycleObserver
import kotlinx.android.synthetic.main.fragment_requests_pager.*
import kotlinx.android.synthetic.main.fragment_requests_pager.layoutTextNetworkNotAvailable
import kotlinx.android.synthetic.main.toolbar.*

class RequestsPagerFragment : BaseFragment(), RequestsView, BaseNetworkWarning {

    @InjectPresenter
    internal lateinit var presenter: RequestsPresenter

    @ProvidePresenter
    fun createRequestsPresenter() = RequestsPresenter()

    private fun sendEventLog(param: String) = presenter.logEvent(param)
    private var requestsVPAdapter: RequestsViewPagerAdapter?  = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Added network change listener
        lifecycle.addObserver(NetworkLifeCycleObserver(this, this))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_requests_pager, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitleText()

        requestsVPAdapter = RequestsViewPagerAdapter(requireFragmentManager())

        val fragmentRequestsActive = RequestsFragment.newInstance(TRANSFER_ACTIVE)
        requestsVPAdapter?.addFragment(fragmentRequestsActive, getString(R.string.LNG_RIDES_ACTIVE))
        val fragmentRequestsAll = RequestsFragment.newInstance(TRANSFER_ARCHIVE)
        requestsVPAdapter?.addFragment(fragmentRequestsAll, getString(R.string.LNG_RIDES_COMPLETED))

        vpRequests.adapter = requestsVPAdapter
        tabs.setupWithViewPager(vpRequests)
        setListenersForLog()
    }

    private fun setTitleText() {
        toolbar_title.text = getString(R.string.LNG_MENU_TITLE_RIDES)
    }

    private fun setListenersForLog() {
        vpRequests.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(p0: Int) {}

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

            override fun onPageSelected(p0: Int) {
                when (p0) {
                    TRANSFER_ACTIVE  -> sendEventLog(getString(R.string.LNG_RIDES_ACTIVE))
                    TRANSFER_ARCHIVE -> sendEventLog(getString(R.string.LNG_RIDES_COMPLETED))
                }
            }
        })
    }

    override fun onNetworkWarning(available: Boolean) {
        layoutTextNetworkNotAvailable.changeViewVisibility(!available)
    }

    @SuppressLint("WrongConstant")
    private class RequestsViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
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
}
