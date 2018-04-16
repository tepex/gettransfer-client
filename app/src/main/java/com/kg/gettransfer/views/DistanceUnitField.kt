package com.kg.gettransfer.views


import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import com.kg.gettransfer.R
import com.kg.gettransfer.modules.ConfigModel
import com.kg.gettransfer.modules.CurrentAccount
import io.reactivex.disposables.CompositeDisposable
import io.realm.RealmList
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject


/**
 * Created by denisvakulenko on 19/03/2018.
 */


class DistanceUnitField : TextView, KoinComponent {
    private val currentAccount: CurrentAccount by inject()
    private val configModel: ConfigModel by inject()

    constructor(c: Context) : super(c)
    constructor(c: Context, attrs: AttributeSet) : super(c, attrs)
    constructor(c: Context, attrs: AttributeSet, defStyle: Int) : super(c, attrs, defStyle)

    protected val disposables = CompositeDisposable()

    init {
        clearListenersFixFocus()

        configModel.updateIfNull()

        setOnClickListener {
            val units = configModel.config?.supportedDistanceUnits

            if (units == null) {
                Toast.makeText(
                        context,
                        context.getString(R.string.list_unavailable),
                        Toast.LENGTH_SHORT).show()
                configModel.updateIfNull()
                return@setOnClickListener
            }

            val fragmentManager = getActivity(context)?.fragmentManager
            val dialog = DistanceUnitDialog(
                    { currentAccount.putAccount(distanceUnit = it) },
                    units)
            dialog.show(fragmentManager, "Distance unit")
        }

        text = currentAccount.accountInfo.distanceUnit

        disposables.addAll(
                currentAccount.addOnAccountChanged {
                    text = currentAccount.accountInfo.distanceUnit
                },
                currentAccount.toastOnError(context))
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
class DistanceUnitDialog(private val f: (String?) -> Unit, private val units: RealmList<String?>)
    : DialogFragment(), KoinComponent {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.distance_unit))
                .setItems(units.toTypedArray(), { _, i -> f(units[i]) })
                .setPositiveButton("Ok", { d, _ -> d.dismiss() })
                .create()
    }
}