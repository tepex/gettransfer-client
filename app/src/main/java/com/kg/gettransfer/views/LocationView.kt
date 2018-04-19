package com.kg.gettransfer.views


import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.widget.Toast
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
    private val model: LocationModel by inject()

    var onLocationChanged: Runnable? = null

    var location: LocationDetailed
        get() = model.location
        set(value) {
            model.location = value
        }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.view_location, this)

        model.onLocationChanged = Runnable {
            tvName.text = location.title

            ibClear.visibility = if (location.isEmpty()) GONE else VISIBLE

            if (location.isValid() || location.isEmpty()) imgInvalidAddress.visibility = GONE

            onLocationChanged?.run()
        }

        model.addOnBusyProgressBar(progressBar, GONE)
        model.addOnBusyChanged { if (it) imgInvalidAddress.visibility = GONE }
        model.addOnError {
            if (location.title.isEmpty()) return@addOnError
            imgInvalidAddress.visibility = VISIBLE
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }

        ibClear.setOnClickListener { model.location = LocationDetailed("") }

        imgInvalidAddress.setOnClickListener { model.validate() }
    }
}