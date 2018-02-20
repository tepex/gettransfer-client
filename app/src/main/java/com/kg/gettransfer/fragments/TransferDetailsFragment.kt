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
import android.view.ViewGroup
import android.widget.Toast
import com.kg.gettransfer.R
import com.kg.gettransfer.modules.Transfers
import com.kg.gettransfer.modules.TransportTypesProvider
import com.kg.gettransfer.modules.http.json.NewTransfer
import com.kg.gettransfer.modules.http.json.PassengerProfile
import com.kg.gettransfer.transfers.TransfersActivity
import com.kg.gettransfer.views.TransportTypesAdapter
import com.kg.gettransfer.views.setupChooseDate
import com.kg.gettransfer.views.setupChooseTime
import kotlinx.android.synthetic.main.fragment_transferdetails.*
import kotlinx.android.synthetic.main.fragment_transferdetails.view.*
import org.koin.android.ext.android.inject


/**
 * Created by denisvakulenko on 12/02/2018.
 */


class TransferDetailsFragment : Fragment() {
    private val transportTypesProvider: TransportTypesProvider by inject()
    private val transfers: Transfers by inject()

    private var savedView: View? = null

    private val pax: Int? get() = etPassengers.text.toString().toIntOrNull()

    private val adapter = TransportTypesAdapter(transportTypesProvider.get(), true)

    var transfer: NewTransfer? = null


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

            v.fabConfirmStep.setOnClickListener { createTransfer(transfer!!) }

            v.etDate.setText(formatDateTime(
                    activity,
                    System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7,
                    FORMAT_SHOW_DATE or FORMAT_ABBREV_MONTH or FORMAT_SHOW_YEAR).toString())

            v.etTime.setText("9:00")

            installEditTextWatcher(v)

            initListTransportTypes(v.rvTypes)

            savedView = v
        }
        return savedView!!
    }


    private fun createTransfer(transfer: NewTransfer) {
        populateNewTransfer(transfer)
        transfers
                .createTransfer(transfer)
                .subscribe(
                        {
                            val intent = Intent(activity, TransfersActivity::class.java)
                            startActivityForResult(intent, 2)
                        },
                        {
                            Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                        })
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
        fabConfirmStep.visibility = if (fieldsValidate()) View.VISIBLE else View.GONE
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
        t.pax = pax!!
        t.transportTypes = adapter.getSelectedIds().toIntArray()

        t.nameSign = etSign.text.toString()
        t.offeredPrice = etPrice.text.toString().toIntOrNull()
        t.flightNumber = etFlightTrainNumber.text.toString()
        t.comment = etComments.text.toString()

        t.passengerProfile = PassengerProfile(etEmail.text.toString(), etPhone.text.toString()).toMap()
    }
}