package com.kg.gettransfer.views


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.maps.model.LatLngBounds
import com.kg.gettransfer.R
import com.kg.gettransfer.data.LocationDetailed
import com.kg.gettransfer.modules.googleapi.GeoAutocompleteProvider
import io.reactivex.functions.Consumer
import org.koin.standalone.KoinComponent
import java.util.*


/**
 * Created by denisvakulenko on 08/02/2018.
 */


class LocationsArrayAdapter(context: Context,
                            private val insertClicked: Consumer<LocationDetailed>,
                            private val bounds: LatLngBounds,
                            private val placeFilter: AutocompleteFilter?)
    : ArrayAdapter<LocationDetailed>(context, R.layout.item_locationautocomplete), Filterable, KoinComponent {

    private val geoAutocompleteProvider: GeoAutocompleteProvider = GeoAutocompleteProvider()

    private val ibInsertCL: View.OnClickListener = View.OnClickListener {
        insertClicked.accept(getItem(it.getTag(R.id.key_position) as Int))
    }

    private var values: ArrayList<LocationDetailed>? = null
        set(newValues) {
            field = newValues
            if (newValues != null) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }


    override fun getCount(): Int {
        return values?.size ?: 0
    }

    override fun getItem(position: Int): LocationDetailed? {
        return values?.get(position)
    }


    override fun clear() {
        values = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(LayoutInflater.from(context), position, convertView, parent)
    }


    private fun createViewFromResource(inflater: LayoutInflater,
                                       position: Int,
                                       convertView: View?,
                                       parent: ViewGroup): View {
        val view: View = convertView
                ?: inflater.inflate(R.layout.item_locationautocomplete, parent, false)

        if (count > position) {
            val item: LocationDetailed = this.getItem(position)!!

            val tvPrimary = view.findViewById<TextView>(R.id.tvPrimary)
            val tvSecondary = view.findViewById<TextView>(R.id.tvSecondary)
            tvPrimary.text = item.title
            tvSecondary.text = item.subtitle

            val ibInsert = view.findViewById<ImageButton>(R.id.ibInsert)
            ibInsert.setTag(R.id.key_position, position)
            ibInsert.setOnClickListener(ibInsertCL)
        }

        view.invalidate()
        view.requestLayout()

        return view
    }


    val autocompleteFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()

            if (constraint != null) {
                val resultList = geoAutocompleteProvider
                        .getPredictions(
                                if (constraint.isBlank()) "Airport" else constraint,
                                bounds,
                                placeFilter)

                if (resultList != null) {
                    results.values = resultList
                    results.count = resultList.size
                }
            }

            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults?) {
            values = if (results != null && results.count > 0 && results.values is ArrayList<*>) {
                results.values as ArrayList<LocationDetailed>
            } else {
                null
            }
        }
    }
}