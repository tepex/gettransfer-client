package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.presenter.RatingLastTripPresenter
import com.kg.gettransfer.presentation.ui.dialogs.BaseBottomSheetDialogFragment
import com.kg.gettransfer.presentation.view.RatingLastTripView
import kotlinx.android.synthetic.main.view_last_trip_rate.*

class RatingLastTripFragment: BaseBottomSheetDialogFragment(), RatingLastTripView {

    override val layout: Int = R.layout.view_last_trip_rate

    @InjectPresenter
    lateinit var presenter: RatingLastTripPresenter

    @ProvidePresenter
    fun providePresenter() = RatingLastTripPresenter()

    companion object {
        fun newInstance() = RatingLastTripFragment()
    }

    override fun initUx(savedInstanceState: Bundle?) {
        super.initUx(savedInstanceState)
        tv_transfer_details.setOnClickListener { presenter.onTransferDetailsClick() }
    }

    override fun setupReviewForLastTrip(transfer: TransferModel, startPoint: LatLng, vehicle: String, color: String, routeModel: RouteModel?) {

    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {

    }

    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {

    }

    override fun setError(e: ApiException) {

    }

    override fun setError(e: DatabaseException) {

    }
}