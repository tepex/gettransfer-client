package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.arellomobile.mvp.MvpAppCompatFragment

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.adapter.RequestsRVAdapter
import com.kg.gettransfer.presentation.presenter.RequestsFragmentPresenter

import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.RequestsFragmentView

import java.text.Format

import kotlinx.android.synthetic.main.fragment_requests.*

import org.koin.android.ext.android.inject

import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportAppNavigator

/**
 * @TODO: Выделить BaseFragment
 */
class RequestsFragment: MvpAppCompatFragment(), RequestsFragmentView {
    @InjectPresenter
    internal lateinit var presenter: RequestsFragmentPresenter

    private val systemInteractor: SystemInteractor by inject()
    private val coroutineContexts: CoroutineContexts by inject()

    private val navigatorHolder: NavigatorHolder by inject()
    private val router: Router by inject()
    //private lateinit var navigator: BaseNavigator = BaseNavigator(this)

    @ProvidePresenter
    fun createRequestsFragmentPresenter() = RequestsFragmentPresenter(coroutineContexts, router, systemInteractor)

    private var categoryName = ""

    companion object {
        @JvmField val CATEGORY   = "category"

        fun newInstance(categoryName: String): RequestsFragment {
            val fragment = RequestsFragment()
            val bundle = Bundle()
            bundle.putString(CATEGORY, categoryName)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments!!
        bundle.getString(CATEGORY)?.let{categoryName = it}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvRequests.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        presenter.setData(categoryName)
    }

    override fun setRequests(transfers: List<Transfer>, distanceUnit: DistanceUnit, dateFormat: Format) {
        rvRequests.adapter = RequestsRVAdapter(transfers, distanceUnit, dateFormat)
    }

    override fun blockInterface(block: Boolean) { (activity as BaseView).blockInterface(block) }

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        (activity as BaseView).setError(finish, errId, *args)
    }
}
