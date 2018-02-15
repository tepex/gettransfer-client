package com.kg.gettransfer.views

import android.content.Context
import android.support.annotation.LayoutRes
import com.google.android.gms.drive.widget.DataBufferAdapter
import com.google.android.gms.location.places.AutocompletePrediction

/**
 * Created by denisvakulenko on 08/02/2018.
 */

class AutocompletePredictionsDataBufferAdapter(c: Context, @LayoutRes r: Int) : DataBufferAdapter<AutocompletePrediction>(c, r) {

}