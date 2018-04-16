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
import com.kg.gettransfer.realm.Currency
import io.reactivex.disposables.Disposable
import io.realm.RealmList
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject


/**
 * Created by denisvakulenko on 19/03/2018.
 */


class CurrencyField : TextView, KoinComponent {
    private val currentAccount: CurrentAccount by inject()
    private val configModel: ConfigModel by inject()

    constructor(c: Context) : super(c)
    constructor(c: Context, attrs: AttributeSet) : super(c, attrs)
    constructor(c: Context, attrs: AttributeSet, defStyle: Int) : super(c, attrs, defStyle)

    val d: Disposable

    init {
        configModel.updateIfNull()

        setOnClickListener {
            val currencies = configModel.config?.supportedCurrencies

            if (currencies == null) {
                Toast.makeText(
                        context,
                        context.getString(R.string.list_unavailable),
                        Toast.LENGTH_SHORT).show()
                configModel.updateIfNull()
                return@setOnClickListener
            }

            val fragmentManager = getActivity(context)?.fragmentManager
            val dialog = CurrencyDialog(
                    { currentAccount.putAccount(currency = it) },
                    currencies)
            dialog.show(fragmentManager, "Currency")
        }

        text = currentAccount.accountInfo.currency

        d = currentAccount.addOnAccountChanged {
            text = currentAccount.accountInfo.currency
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
class CurrencyDialog(private val f: (String?) -> Unit, private val currencies: RealmList<Currency?>)
    : DialogFragment(), KoinComponent {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.currency))
                .setItems(currencies.map { it?.isoCode }.toTypedArray(), { _, i -> f(currencies[i]?.isoCode) })
                .setPositiveButton("Ok", { d, _ -> d.dismiss() })
                .create()
    }
}