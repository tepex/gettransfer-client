package com.kg.gettransfer.fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.kg.gettransfer.R
import com.kg.gettransfer.data.LocationDetailed
import com.kg.gettransfer.adapter.LocationsArrayAdapter
import com.kg.gettransfer.view.base.setupClearButtonWithAction
import io.reactivex.functions.Consumer


/**
 * Created by denisvakulenko on 06/02/2018.
 */


class ChooseLocationFragment : Fragment() {
    private val imm: InputMethodManager by lazy {
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private val LAT_LNG_BOUNDS_WORLD = LatLngBounds(
            LatLng(-90.0, -180.0),
            LatLng(90.0, 180.0))

    private val BOUNDS_MOUNTAIN_VIEW = LatLngBounds(
            LatLng(37.398160, -122.180831),
            LatLng(37.430610, -121.972090))

    private var etLocation: EditText? = null
    private var rvLocations: ListView? = null

    private val adapter by lazy {
        val activity = activity ?: throw Throwable("activity is null")
        LocationsArrayAdapter(
                activity,
                Consumer { l ->
                    location = l
                },
                LAT_LNG_BOUNDS_WORLD,
                AutocompleteFilter
                        .Builder()
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                        .build())
    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            adapter.autocompleteFilter.filter(s)
        }
    }


    lateinit var consumer: Consumer<LocationDetailed?>

    var location: LocationDetailed? = null
        set(value) {
            field = value
            updateText(value)
        }


    private fun updateText(location: LocationDetailed?) {
        etLocation?.setText(location?.title ?: "")
        etLocation?.setSelection(etLocation?.text?.length ?: 0)
    }


    private var savedview: View? = null


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        if (savedview == null) {
            val view = inflater.inflate(
                    R.layout.fragment_chooselocation, container, false)

            etLocation = view.findViewById(R.id.etLocation)
            rvLocations = view.findViewById(R.id.rvLocations)

            etLocation?.setupClearButtonWithAction()
            etLocation?.addTextChangedListener(textWatcher)
            etLocation?.setOnKeyListener(View.OnKeyListener { _, k, e ->
                if (e.action == KeyEvent.ACTION_DOWN) {
                    if (k == KeyEvent.KEYCODE_DPAD_CENTER || k == KeyEvent.KEYCODE_ENTER) {
                        consumer.accept(LocationDetailed(etLocation?.text.toString()))
                        return@OnKeyListener true
                    }
                }
                false
            })

            rvLocations?.adapter = adapter
            rvLocations?.emptyView = view.findViewById(R.id.tvEmpty)
            rvLocations?.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        consumer.accept(adapter.getItem(position))
                    }

            view.findViewById<ImageButton>(R.id.ibBack)
                    .setOnClickListener { consumer.accept(null) }
            view.findViewById<ImageButton>(R.id.ibOk)
                    .setOnClickListener {
                        consumer.accept(LocationDetailed(etLocation?.text.toString()))
                    }

            this.savedview = view
        }
        return savedview!!
    }


    fun setHint(hint: CharSequence?) {
        etLocation?.hint = hint
    }


    override fun onPause() {
        super.onPause()
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()
        adapter.clear()
        updateText(location)
        etLocation?.requestFocus()
        etLocation?.selectAll()
        imm.showSoftInput(etLocation, 0)
    }
}