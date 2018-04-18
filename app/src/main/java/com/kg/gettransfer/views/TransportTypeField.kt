package com.kg.gettransfer.views


import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.EditText
import com.kg.gettransfer.R
import com.kg.gettransfer.modules.RouteInfoModel
import com.kg.gettransfer.modules.TransportTypes
import com.kg.gettransfer.modules.http.json.NewTransfer
import com.kg.gettransfer.modules.http.json.PriceRange
import com.kg.gettransfer.realm.TransportType
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject


/**
 * Created by denisvakulenko on 19/03/2018.
 */


class TransportTypeField : EditText, KoinComponent {
    private val pricesModel: RouteInfoModel by inject()
    private val types: TransportTypes by inject()


    val checked = BooleanArray(types.typesMap.size, { _ -> false })

    val typesIDs: Array<String>
        get() = checked
                .mapIndexed { index, b ->
                    if (b) types.typesList[index].id
                    else null
                }
                .filterNotNull()
                .toTypedArray()


    constructor(c: Context) : super(c)
    constructor(c: Context, attrs: AttributeSet) : super(c, attrs)
    constructor(c: Context, attrs: AttributeSet, defStyle: Int) : super(c, attrs, defStyle)

    init {
        clearListenersFixFocus()

        setOnClickListener {
            val fragmentManager = getActivity(context)?.fragmentManager
            val dialog = TransportTypeDialog(this, pricesModel.info?.prices)
            dialog.show(fragmentManager, "Type selection")
        }

        pricesModel.toastOnError(context, context.getString(R.string.unable_to_load_prices))
    }


    fun updatePrices(transfer: NewTransfer) = pricesModel.get(transfer)

    fun clear() = checked.fill(false)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.actionMasked == MotionEvent.ACTION_UP) {
            requestFocus()
            performClick()
        }

        return true
    }
}


@SuppressLint("ValidFragment")
class TransportTypeDialog(
        private val tf: TransportTypeField,
        private val prices: Map<TransportType, PriceRange>?)
    : DialogFragment(), KoinComponent {

    private val types: TransportTypes by inject()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val adapter = TransportTypesArrayAdapter(
                activity, types.typesList, tf.checked, prices)

        val d = AlertDialog.Builder(activity)
                .setTitle(getString(R.string.transport))
                .setAdapter(adapter, null)
                .setPositiveButton("Ok", { d, _ -> d.dismiss() })
                .create()

        d.setOnShowListener {
            (d as AlertDialog).listView
                    .setOnItemClickListener { _, view, i, _ ->
                        tf.checked[i] = !tf.checked[i]
                        updateText()
                        val viewHolder = view.tag as TransportTypesArrayAdapter.ViewHolder
                        viewHolder.setSelected(tf.checked[i])
                    }
        }

        return d
    }

    private fun updateText() {
        var text = ""
        types.typesList.forEachIndexed { i, s -> if (tf.checked[i]) text += ", ${s.title}" }
        tf.setText(if (text.isNotEmpty()) text.substring(2) else "")
    }
}