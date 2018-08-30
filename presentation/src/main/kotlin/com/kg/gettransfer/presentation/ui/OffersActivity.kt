package com.kg.gettransfer.presentation.ui

import android.graphics.Color
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.Toolbar
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.OffersPresenter
import com.kg.gettransfer.presentation.view.OffersView
import kotlinx.android.synthetic.main.activity_offers.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_offer.view.*
import android.graphics.PorterDuff
import com.kg.gettransfer.R.id.ratingBar
import android.graphics.drawable.LayerDrawable
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.presentation.presenter.CreateOrderPresenter
import kotlinx.android.synthetic.main.view_offer.*
import org.koin.android.ext.android.inject


class OffersActivity: MvpAppCompatActivity(), OffersView {
    @InjectPresenter
    internal lateinit var presenter: OffersPresenter

    private val apiInteractor: ApiInteractor by inject()
    private val coroutineContexts: CoroutineContexts by inject()

    @ProvidePresenter
    fun createOffersPresenter(): OffersPresenter = OffersPresenter(coroutineContexts, apiInteractor)

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_offers)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).toolbar_title.setText(R.string.carrier_offers)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }

        setOnClickListeners()

        val stars = ratingBarOffer.progressDrawable as LayerDrawable
        stars.getDrawable(2).setColorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)
    }

    fun setOnClickListeners(){
        layoutTransferRequestInfo.setOnClickListener{}
    }

    override fun onBackPressed() {
        presenter.onBackCommandClick()
    }
}