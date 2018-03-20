package com.kg.gettransfer.views


import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import com.kg.gettransfer.R
import com.kg.gettransfer.data.LocationDetailed
import com.kg.gettransfer.modules.LocationModel
import kotlinx.android.synthetic.main.view_location.view.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject


/**
 * Created by denisvakulenko on 09/02/2018.
 */


class LocationView : ConstraintLayout, KoinComponent {
    private val locationModel: LocationModel by inject()

    var onLocationChanged: Runnable? = null

    var location
        get() = locationModel.location
        set(value) {
            locationModel.location = value
        }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.view_location, this)

        locationModel.onLocationChanged = Runnable {
            val text = locationModel.location?.title
            tvName.text = text ?: ""
            ibClear.visibility =
                    if (text?.isNotEmpty() == true) View.VISIBLE
                    else View.GONE
            onLocationChanged?.run()
        }

        locationModel.addOnBusyProgressBar(progressBar)

        ibClear.setOnClickListener { locationModel.location = LocationDetailed("") }
    }
}