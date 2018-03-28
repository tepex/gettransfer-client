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
import android.widget.Toast
import com.kg.gettransfer.modules.ConfigModel
import com.kg.gettransfer.modules.CurrentAccount
import io.reactivex.disposables.Disposable
import io.realm.RealmList
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject


/**
 * Created by denisvakulenko on 19/03/2018.
 */


class DistanceUnitField : EditText, KoinComponent {
    private val currentAccount: CurrentAccount by inject()
    private val configModel: ConfigModel by inject()

    constructor(c: Context) : super(c)
    constructor(c: Context, attrs: AttributeSet) : super(c, attrs)
    constructor(c: Context, attrs: AttributeSet, defStyle: Int) : super(c, attrs, defStyle)

    val d: Disposable

    init {
        clearListenersFixFocus()

        configModel.updateIfNull()

        setOnClickListener {
            val units = configModel.config?.supportedDistanceUnits

            if (units == null) {
                Toast.makeText(
                        context,
                        "Try again, when Internet available",
                        Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fragmentManager = getActivity(context)?.fragmentManager
            val dialog = DistanceUnitDialog(this, units)
            dialog.show(fragmentManager, "Distance unit")
        }

        setText(currentAccount.accountInfo.distanceUnit)

        d = currentAccount.addOnAccountChanged {
            setText(currentAccount.accountInfo.distanceUnit)
        }
    }

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
class DistanceUnitDialog(private val cf: DistanceUnitField, private val units: RealmList<String?>)
    : DialogFragment(), KoinComponent {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val d = AlertDialog.Builder(activity)
                .setTitle("Distance unit")
                .setItems(units.toTypedArray(), { d, i ->
                    cf.setText(units[i])
                })
                .setPositiveButton("Ok", { d, _ -> d.dismiss() })
                .create()

        return d
    }
}