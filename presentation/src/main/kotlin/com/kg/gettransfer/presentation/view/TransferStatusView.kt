package com.kg.gettransfer.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import com.kg.gettransfer.R
import com.kg.gettransfer.realm.Transfer
import com.kg.gettransfer.realm.getPlString

/**
 * Created by denisvakulenko on 22/02/2018.
 */

class TransferStatusView : TextView {
    constructor(c: Context) : super(c)
    constructor(c: Context, attrs: AttributeSet) : super(c, attrs)
    constructor(c: Context, attrs: AttributeSet, defStyle: Int) : super(c, attrs, defStyle)

    init {
        gravity = Gravity.CENTER
        minWidth = (40 * resources.displayMetrics.density).toInt()
    }

    fun update(item: Transfer) {
        when {
            item.isActiveNew() -> {
                setTextColor(0xff000000.toInt())
                setBackgroundResource(R.drawable.bg_rounded_2_yellow)
                val offers = item.offersCount
                text =
                        if (offers > 0)
                            offers.toString() + " " + context.getPlString(R.string.pl_offers).forN(offers)
                        else context.getString(R.string.status_long_connecting)
            }
            item.isActiveConfirmed() -> {
                setTextColor(0xffffffff.toInt())
                setBackgroundResource(R.drawable.bg_rounded_2_blue)
                text = item.strStatus(context)
            }
            else -> {
                setTextColor(0xaa000000.toInt())
                setBackgroundResource(R.drawable.bg_rounded_2_lightgray)
                text = item.strStatus(context)
            }
        }
    }
}