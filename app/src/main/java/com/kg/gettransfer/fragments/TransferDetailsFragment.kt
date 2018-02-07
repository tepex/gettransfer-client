package com.kg.gettransfer.fragments


import android.app.Fragment
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateUtils.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.kg.gettransfer.R
import com.kg.gettransfer.modules.TransportTypesProvider
import com.kg.gettransfer.modules.network.json.NewTransfer
import com.kg.gettransfer.modules.network.json.PassengerProfile
import com.kg.gettransfer.views.TransportTypesAdapter
import com.kg.gettransfer.views.setupChooseDate
import com.kg.gettransfer.views.setupChooseTime
import org.koin.android.ext.android.inject


/**
 * Created by denisvakulenko on 12/02/2018.
 */


class TransferDetailsFragment : Fragment() {
    private val transportTypesProvider: TransportTypesProvider by inject()

    val etDate by lazy { view.findViewById<EditText>(R.id.etDate) }
    val etTime by lazy { view.findViewById<EditText>(R.id.etTime) }
    val etPassengers by lazy { view.findViewById<EditText>(R.id.etPassengers) }

    val etEmail by lazy { view.findViewById<EditText>(R.id.etEmail) }
    val etPhone by lazy { view.findViewById<EditText>(R.id.etPhone) }

    val fab by lazy { view.findViewById<FloatingActionButton>(R.id.fabConfirmStep) }

    var savedview: View? = null


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        if (savedview == null) savedview =
                inflater.inflate(R.layout.fragment_transferdetails, container, false)
        return savedview!!
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etDate.setupChooseDate()
        etTime.setupChooseTime()

        this.view.findViewById<Button>(R.id.btnPassengersDec).setOnClickListener { passengersDec() }
        this.view.findViewById<Button>(R.id.btnPassengersInc).setOnClickListener { passengersInc() }

        initListTransportTypes()
        installEditTextWatcher()

        etDate.setText(formatDateTime(
                activity,
                System.currentTimeMillis(),
                FORMAT_SHOW_DATE or FORMAT_ABBREV_MONTH).toString())

        etTime.setText("9:00")
    }

    val textWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            fab.visibility = if (validateFields()) View.VISIBLE else View.GONE
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    private fun installEditTextWatcher() {
        etDate.addTextChangedListener(textWatcher)
        etTime.addTextChangedListener(textWatcher)
        etPassengers.addTextChangedListener(textWatcher)

        etEmail.addTextChangedListener(textWatcher)
        etPhone.addTextChangedListener(textWatcher)
    }


    private fun validateFields(): Boolean =
            android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.text).matches() &&
                    android.util.Patterns.PHONE.matcher(etPhone.text).matches()


    fun passengersInc() {
        val n = etPassengers.text.toString().toIntOrNull() ?: 0
        etPassengers.setText((n + 1).toString())
    }

    fun passengersDec() {
        val n = etPassengers.text.toString().toIntOrNull() ?: 0
        etPassengers.setText((Math.max(1, n - 1)).toString())
    }


    private fun initListTransportTypes() {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvTypes)

        val types = transportTypesProvider.get()
        val adapter = TransportTypesAdapter(types, true)

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter
    }


    public fun populateNewTransfer(t: NewTransfer) {
        t.dateTo = etDate.text.toString()
        t.timeTo = etTime.text.toString()
        t.transportTypes = intArrayOf(0)
        t.pax = etPassengers.text.toString().toIntOrNull() ?: 0 // TODO: Assert here later
        t.nameSign = "Sign"
        t.passengerProfile = PassengerProfile(etEmail.text.toString(), etPhone.text.toString()).toMap()
    }
}