package com.kg.gettransfer.transfer


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.kg.gettransfer.R
import com.kg.gettransfer.modules.Transfers
import com.kg.gettransfer.modules.TransportTypesProvider
import com.kg.gettransfer.realm.Transfer
import com.kg.gettransfer.realm.Utils
import com.kg.gettransfer.views.OffersAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_transfer.*
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 16/02/2018.
 */


class TransferActivity : AppCompatActivity(), KoinComponent {
    private val transfers: Transfers by inject()
    private val transportTypes: TransportTypesProvider by inject()

    private var id: Int = -1

    private val transferRealmResults by lazy { transfers.getAsync(id) }

    private var transfer: Transfer? = null
        set(newTransfer) {
            field = newTransfer
            if (newTransfer != null) updateUI(newTransfer)
        }

    private val transferChangeListener: (RealmResults<Transfer>) -> Unit = { t ->
        Log.i("TransferActivity", "getAsync() changed")
        if (t.size > 0 && t.isLoaded) {
            val r = t[0]
            if (r?.isLoaded == true) {
                transfer = r
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("TransferActivity", "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        id = intent.getIntExtra("id", -1)

        transferRealmResults.addChangeListener(transferChangeListener)

        transfers.updateOffers(id)

        val rv = rvOffers
        rv.layoutManager = LinearLayoutManager(applicationContext)
        rv.emptyView = tvNoOffers

        wv.loadUrl("https://gettransfer.com/en")
    }

    private fun updateUI(transfer: Transfer) {
        tvTitle.text = "Transfer #" + transfer.id

        tvFrom.text = transfer.from?.name
        tvTo.text = transfer.to?.name

        tvDate.text = Utils.dateToString(this, transfer.dateTo)

        tvPassengers.text = transfer.pax.toString()
        tvTypes.text = transportTypes.getNames(transfer.transportTypes)
        tvSign.text = transfer.nameSign
        tvChildSeats.text = transfer.childSeats
        tvComments.text = transfer.comments ?: "-"

        if (transfer.status == "new" && !transfer.bookNow) {
            clActive.visibility = View.VISIBLE
            clArchive.visibility = View.GONE

            val offers = transfers.getOffers(transfer)!!
            rvOffers.adapter = OffersAdapter(offers, true)

            offers.addChangeListener { offers ->
                val hasOffers = offers.size > 0
                tvChooseCarrier.visibility = if (hasOffers) View.VISIBLE else View.GONE
                tvConnecting.visibility = if (!hasOffers) View.VISIBLE else View.GONE
            }
        } else {
            clActive.visibility = View.GONE
            clArchive.visibility = View.VISIBLE
        }

        transferStatusView.update(transfer)

        Log.i("TransferActivity", "UI updated")
    }

    fun back(v: View) {
        finish()
    }

    fun cancel(v: View) {
        transfers.cancelTransfer(id)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { btnCancel.isEnabled = false }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    btnCancel.isEnabled = true
                    if (response.success) {
//                        transfer = response.data

                        val realm = Realm.getDefaultInstance()
                        realm.beginTransaction()
                        transfer?.status = "canceled"
                        transfer?.updateIsActive()
                        realm.commitTransaction()
                        realm.close()

                        updateUI(transfer ?: return@subscribe)

                        scrollView.post { scrollView.fullScroll(View.FOCUS_UP) }
                    }
                }, {
                    btnCancel.isEnabled = true
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                })
    }

    fun restore(v: View) {
        transfers.restoreTransfer(id)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { btnRestore.isEnabled = false }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    btnRestore.isEnabled = true
                    if (response.success) {
//                        transfer = response.data

                        val realm = Realm.getDefaultInstance()
                        realm.beginTransaction()
                        transfer?.status = "new"
                        transfer?.updateIsActive()
                        realm.commitTransaction()
                        realm.close()

                        updateUI(transfer ?: return@subscribe)

                        scrollView.post { scrollView.fullScroll(View.FOCUS_UP) }
                    }
                }, {
                    btnRestore.isEnabled = true
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        transferRealmResults.removeAllChangeListeners()
    }
}