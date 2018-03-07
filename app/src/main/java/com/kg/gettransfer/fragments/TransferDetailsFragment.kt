package com.kg.gettransfer.fragments


import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateUtils.*
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import com.kg.gettransfer.R
import com.kg.gettransfer.modules.PricesPreviewModel
import com.kg.gettransfer.modules.Transfers
import com.kg.gettransfer.modules.TransportTypesProvider
import com.kg.gettransfer.modules.http.json.NewTransfer
import com.kg.gettransfer.modules.http.json.PassengerProfile
import com.kg.gettransfer.views.TransportTypesAdapter
import com.kg.gettransfer.views.setupChooseDate
import com.kg.gettransfer.views.setupChooseTime
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_transferdetails.*
import kotlinx.android.synthetic.main.fragment_transferdetails.view.*
import org.koin.android.ext.android.inject
import java.util.*


/**
 * Created by denisvakulenko on 12/02/2018.
 */


class TransferDetailsFragment : Fragment() {
    private val transportTypesProvider: TransportTypesProvider by inject()
    private val transfers: Transfers by inject()
    private val pricePreview: PricesPreviewModel by inject()


    private var savedView: View? = null

    private val pax: Int? get() = etPassengers.text.toString().toIntOrNull()

    private val adapter = TransportTypesAdapter(transportTypesProvider.get(), true)

    private val disposables = CompositeDisposable()

    lateinit var transfer: NewTransfer


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        if (savedView == null) {
            val v = inflater.inflate(
                    R.layout.fragment_transferdetails,
                    container,
                    false)!!

            v.btnPassengersDec.setOnClickListener { passengersDec() }
            v.btnPassengersInc.setOnClickListener { passengersInc() }

            v.etDate.setupChooseDate()
            v.etTime.setupChooseTime()

            v.fabConfirmStep.setOnClickListener { createTransfer(transfer) }

            v.etDate.setText(formatDateTime(
                    activity,
                    System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7,
                    FORMAT_SHOW_DATE or FORMAT_ABBREV_MONTH or FORMAT_SHOW_YEAR).toString())

            v.etTime.setText("9:00")

            installEditTextWatcher(v)

            initListTransportTypes(v.rvTypes)

            savedView = v
        }

        savedView!!.post {
            savedView!!.scrollView.fullScroll(FOCUS_UP)
            savedView!!.scrollView.scrollTo(0, 0)
        }

        pricePreview.get(
                transfer.from["point"]!!,
                transfer.to["point"]!!,
                transfer.distance,
                false,
                Date())

        return savedView!!
    }


    private fun createTransfer(transfer: NewTransfer) {
        populateNewTransfer(transfer)
        disposables.add(
                transfers.createTransfer(transfer)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe { setBusy(true) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    setBusy(false)
                                    reset()
                                    val intent = Intent(activity, TransfersFragment::class.java)
                                    startActivityForResult(intent, 2)
                                },
                                {
                                    setBusy(false)
                                    tvError.text = "Error creating request.\n" + it.message
                                    fabConfirmStep.visibility = VISIBLE
                                    scrollView.post {
                                        scrollView.fullScroll(View.FOCUS_DOWN)
                                    }
                                }))
    }


    private fun setBusy(b: Boolean) {
        if (b) {
            fabConfirmStep.visibility = INVISIBLE
            progressBar.visibility = VISIBLE
            fabProgress.visibility = VISIBLE
            tvError.text = ""
        } else {
            progressBar.visibility = INVISIBLE
            fabProgress.visibility = INVISIBLE
        }
    }


    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            fieldsUpdated()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    }


    private fun installEditTextWatcher(v: View) {
        v.etDate.addTextChangedListener(textWatcher)
        v.etTime.addTextChangedListener(textWatcher)
        v.etPassengers.addTextChangedListener(textWatcher)

        v.etSign.addTextChangedListener(textWatcher)

        v.etEmail.addTextChangedListener(textWatcher)
        v.etPhone.addTextChangedListener(textWatcher)
    }


    private fun fieldsUpdated() {
        fabConfirmStep.visibility = if (fieldsValidate()) VISIBLE else GONE
    }


    private fun fieldsValidate(): Boolean {
        var message = ""

        if (etDate.text.isEmpty()) {
            message += ", date"
        }
        if (etTime.text.isEmpty()) {
            message += ", date"
        }
        if ((pax ?: 0) < 1) {
            message += ", passengers"
        }

        if (etSign.text.isEmpty()) {
            message += ", name sign"
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.text).matches()) {
            message += ", email"
        }
        if (!android.util.Patterns.PHONE.matcher(etPhone.text).matches()) {
            message += ", phone"
        }

        if (message.isNotEmpty()) {
            message = "Fill" + message.substring(1) + " to get offers."
            tvError.text = message
            return false
        } else {
            tvError.text = " "
            return true
        }
    }


    private fun passengersInc() {
        etPassengers.setText(((pax ?: 0) + 1).toString())
        etPassengers.requestFocus()
        etPassengers.selectAll()
    }

    private fun passengersDec() {
        etPassengers.setText((Math.max(1, (pax ?: 0) - 1)).toString())
        etPassengers.requestFocus()
        etPassengers.selectAll()
    }


    private fun initListTransportTypes(recyclerView: RecyclerView) {
        val layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.HORIZONTAL,
                false)

        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter
    }


    private fun populateNewTransfer(t: NewTransfer) {
        t.dateTo = etDate.text.toString()
        t.timeTo = etTime.text.toString()
        t.pax = pax ?: 0
        t.transportTypes = adapter.getSelectedIds().toIntArray()

        t.nameSign = etSign.text.toString()
        t.offeredPrice = etPrice.text.toString().toIntOrNull()
        t.flightNumber = etFlightTrainNumber.text.toString()
        t.comment = etComments.text.toString()

        t.passengerProfile = PassengerProfile(etEmail.text.toString(), etPhone.text.toString()).toMap()
    }


    private fun reset() {
        etPassengers.setText("")
        etPrice.setText("")
        etFlightTrainNumber.setText("")
        etFlightTrainNumber.setText("")
    }
}