package com.kg.gettransfer.views


import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.kg.gettransfer.data.LocationDetailed


/**
 * Created by denisvakulenko on 09/02/2018.
 */


class LocationView : TextView {
    constructor(context: Context, attrs: AttributeSet)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    var location: LocationDetailed? = null
        set(value) {
            field = value
            text = value?.title ?: ""
            onTextChanged(text, 0, 0, 0)
        }
}