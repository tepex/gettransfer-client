package com.kg.gettransfer.presentation.ui

import com.kg.gettransfer.R

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatDelegate
import android.view.View
import android.view.WindowManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.presentation.presenter.SelectFinishPointPresenter
import com.kg.gettransfer.presentation.view.SelectFinishPointView
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class SelectFinishPointActivity: BaseGoogleMapActivity(), SelectFinishPointView{
    @InjectPresenter
    internal lateinit var presenter: SelectFinishPointPresenter

    private val routeInteractor: RouteInteractor by inject()

    @ProvidePresenter
    fun createSelectFinishPointPresenter(): SelectFinishPointPresenter = SelectFinishPointPresenter(coroutineContexts,
                                                                                                    router,
                                                                                                    systemInteractor,
                                                                                                    routeInteractor)

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun getPresenter(): SelectFinishPointPresenter = presenter

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_finish_point)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) window.statusBarColor = Color.TRANSPARENT
        else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            viewGradient.visibility = View.GONE
        }

        _mapView = mapView
        initGoogleMap(savedInstanceState)
    }
}