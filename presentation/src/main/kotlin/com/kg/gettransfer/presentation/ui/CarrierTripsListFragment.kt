package com.kg.gettransfer.presentation.ui

import com.kg.gettransfer.R
import android.os.Bundle

import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import com.google.android.material.tabs.TabLayout

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.*

import com.arellomobile.mvp.MvpAppCompatFragment

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException

import com.kg.gettransfer.presentation.adapter.CarrierTripsRVAdapter

import com.kg.gettransfer.presentation.model.CarrierTripsRVItemModel

import com.kg.gettransfer.presentation.presenter.CarrierTripsListPresenter
import com.kg.gettransfer.presentation.view.BaseView

import com.kg.gettransfer.presentation.view.CarrierTripsListFragmentView

import kotlinx.android.synthetic.main.activity_carrier_trips_list_fragment.*
import leakcanary.AppWatcher

import timber.log.Timber

class CarrierTripsListFragment : MvpAppCompatFragment(), CarrierTripsListFragmentView {
    @InjectPresenter
    internal lateinit var presenter: CarrierTripsListPresenter

    private var isUserScrolling = false

    @ProvidePresenter
    fun createCarrierTripsListPresenter() = CarrierTripsListPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.activity_carrier_trips_list_fragment, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvTrips.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        initTabLayout()
    }

    private fun initTabLayout(){
        tabsForRecyclerView.addTab(tabsForRecyclerView.newTab().setText(getString(R.string.LNG_TRIPS_TODAY)))
        tabsForRecyclerView.addTab(tabsForRecyclerView.newTab().setText(getString(R.string.LNG_TRIPS_ALL)))
        tabsForRecyclerView.addTab(tabsForRecyclerView.newTab().setText(getString(R.string.LNG_TRIPS_COMPLETED)))
    }

    override fun setTrips(tripsItems: List<CarrierTripsRVItemModel>, startTodayPosition: Int, endTodayPosition: Int) {
        rvTrips.adapter = CarrierTripsRVAdapter(presenter, tripsItems)
        rvTrips.scrollToPosition(startTodayPosition)

        rvTrips.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val itemPosition = (rvTrips.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()

                if (isUserScrolling) {
                    val tab = when {
                        itemPosition in startTodayPosition..endTodayPosition -> tabsForRecyclerView.getTabAt(0)
                        itemPosition > endTodayPosition                      -> tabsForRecyclerView.getTabAt(1)
                        itemPosition < startTodayPosition                    -> tabsForRecyclerView.getTabAt(2)
                        else                                                 -> null
                    }
                    tab?.select()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> isUserScrolling = true
                    RecyclerView.SCROLL_STATE_IDLE     -> isUserScrolling = false
                }
            }
        })
        tabsForRecyclerView.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {  }
            override fun onTabUnselected(p0: TabLayout.Tab?) {  }
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (isUserScrolling) return
                when (tab.position) {
                    0 -> (rvTrips.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(startTodayPosition, 0)
                    1 -> if (tripsItems.size >= endTodayPosition) (rvTrips.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(endTodayPosition, 0)
                    2 -> if (startTodayPosition > 0) (rvTrips.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(0, 0)
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        AppWatcher.objectWatcher.watch(this)
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) =
            (activity as BaseView).blockInterface(block, useSpinner)

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) =
            (activity as BaseView).setError(finish, errId, *args)

    override fun setError(e: ApiException) {
        Timber.e("code: ${e.code}", e)
        Utils.showError(context!!, false, "${getString(R.string.LNG_ERROR)}: ${e.message}")
    }

    override fun setError(e: DatabaseException) =
        (activity as BaseView).setError(e)

    override fun setTransferNotFoundError(transferId: Long) {}
}
