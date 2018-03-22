package com.kg.gettransfer.fragment


import android.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.kg.gettransfer.R
import com.kg.gettransfer.mainactivity.MainActivity
import com.kg.gettransfer.modules.CurrentAccount
import com.kg.gettransfer.modules.PromoCodeModel
import com.kg.gettransfer.modules.TransfersModel
import com.kg.gettransfer.modules.http.json.NewTransfer
import com.kg.gettransfer.modules.http.json.PassengerProfile
import com.kg.gettransfer.modules.http.json.Trip
import com.kg.gettransfer.realm.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_transferdetails.*
import kotlinx.android.synthetic.main.fragment_transferdetails.view.*
import org.koin.android.ext.android.inject


/**
 * Created by denisvakulenko on 12/02/2018.
 */


class TransferDetailsFragment : Fragment() {
    private val transfersModel: TransfersModel by inject()
    private val promoCodeModel: PromoCodeModel by inject()
    private val currentAccount: CurrentAccount by inject()

    private var savedView: View? = null

    private val pax: Int? get() = etPassengers.text.toString().toIntOrNull()

    private val disposables = CompositeDisposable()

    private lateinit var transfer: NewTransfer

    fun setTransfer(t: NewTransfer) {
        transfer = t
        val v = savedView ?: return
        v.tvFromDetails.text = transfer.from?.name
        v.tvToDetails.text =
                transfer.to?.name ?: Utils.hoursToString(transfer.hireDuration ?: 0)

    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        var v = savedView

        if (v == null) {
            v = inflater.inflate(
                    R.layout.fragment_transferdetails,
                    container,
                    false)!!

            v.btnPassengersDec.setOnClickListener { passengersDec() }
            v.btnPassengersInc.setOnClickListener { passengersInc() }

            v.fabCreate.setOnClickListener { createTransfer(transfer) }

            v.btnHavePromoCode.setOnClickListener {
                btnHavePromoCode.visibility = GONE
                clPromoCode.visibility = VISIBLE
                etPromoCode.requestFocus()
            }

            v.tfTransport.updatePrices(transfer)

            installEditTextWatcher(v)

            initPromoCodeUI(v.etPromoCode, v.tvPromoInfo, v.tvPromoValidation, v.pbPromoValidation)

            v.btnBack.setOnClickListener { fragmentManager.popBackStackImmediate() }

            v.tvFromDetails.text = transfer.from?.name
            v.tvToDetails.text =
                    transfer.to?.name ?: Utils.hoursToString(transfer.hireDuration ?: 0)

            savedView = v
        }

        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etEmail.setText(currentAccount.email)

        view.post {
            scrollView.fullScroll(FOCUS_UP)
            scrollView.scrollTo(0, 0)
        }

        if (savedView != null) savedView?.tfTransport?.updatePrices(transfer)
    }


    private fun createTransfer(transfer: NewTransfer) {
        populateNewTransfer(transfer)
        disposables.add(
                transfersModel.createTransfer(transfer)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe { setBusy(true) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    setBusy(false)
                                    reset()

                                    val act = activity as MainActivity // TODO: Callbacks here

                                    fragmentManager.popBackStackImmediate()

                                    act.showTransfers(null)
                                },
                                {
                                    setBusy(false)
                                    tvError.text = "Error creating request.\n" + it.message
                                    fabCreate.visibility = VISIBLE
                                    scrollView.post {
                                        scrollView.fullScroll(FOCUS_DOWN)
                                    }
                                }))
    }


    private fun setBusy(b: Boolean) {
        if (b) {
            fabCreate.visibility = INVISIBLE
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

        v.tfTransport.addTextChangedListener(textWatcher)
    }


    private fun fieldsUpdated() {
        fabCreate.visibility = if (fieldsValidate()) VISIBLE else GONE
    }


    private fun fieldsValidate(): Boolean {
        var message = ""

        if (etDate.text.isEmpty()) {
            message += ", date"
        }
        if (etTime.text.isEmpty()) {
            message += ", time"
        }
        if ((pax ?: 0) < 1) {
            message += ", passengers"
        }

        if (tfTransport.typesIDs.isEmpty()) {
            message += ", transport"
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
            message = "Please fill" + message.substring(1) + " to get offers."
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


    private fun initPromoCodeUI(
            etPromoCode: EditText,
            tvPromoInfo: TextView,
            tvPromoValidation: TextView,
            pbPromoValidation: ProgressBar) {

        val promoEditTextWatcher: TextWatcher = object : TextWatcher {
            private val DELAY: Long = 500

            val handler = Handler(Looper.getMainLooper()) // UI thread
            var workRunnable: Runnable? = null

            override fun afterTextChanged(s: Editable?) {
                handler.removeCallbacks(workRunnable)
                if (s.isNullOrEmpty()) {
                    promoCodeModel.code = s.toString()
                } else {
                    workRunnable = Runnable { promoCodeModel.code = s.toString() }
                    handler.postDelayed(workRunnable, DELAY)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        }

        etPromoCode.addTextChangedListener(promoEditTextWatcher)

        promoCodeModel.addOnInfoUpdated {
            if (it.data != null) {
                tvPromoInfo.visibility = VISIBLE
                tvPromoInfo.text = it.data
            } else {
                tvPromoInfo.visibility = GONE
            }
            tvPromoValidation.visibility = GONE
        }
        promoCodeModel.addOnError {
            tvPromoInfo.visibility = GONE
            tvPromoValidation.visibility = VISIBLE
            tvPromoValidation.text = it.message
        }
        promoCodeModel.addOnBusyProgressBar(pbPromoValidation)
        promoCodeModel.addOnBusyChanged {
            if (it) tvPromoInfo.visibility = GONE
        }
    }


    private fun populateNewTransfer(t: NewTransfer) {
        t.dateTo = Trip(etDate.text.toString(), etTime.text.toString())
        t.pax = pax ?: 0
        t.transportTypes = tfTransport.typesIDs

        t.nameSign = etSign.text.toString()
        t.offeredPrice = etPrice.text.toString().toIntOrNull()
        t.flightNumber = etFlightTrainNumber.text.toString()
        t.comment = etComments.text.toString()

        t.passengerProfile = PassengerProfile(etEmail.text.toString(), etPhone.text.toString()).toMap()

        t.promoCode = etPromoCode.text.toString()
    }


    private fun reset() {
        etDate.editableText.clear()
        etTime.editableText.clear()

        etPassengers.editableText.clear()

        tfTransport.clear()

        etSign.editableText.clear()
        etPrice.editableText.clear()
        etFlightTrainNumber.editableText.clear()
        etComments.editableText.clear()

        clPromoCode.visibility = GONE
        tvPromoValidation.visibility = GONE
        btnHavePromoCode.visibility = VISIBLE
    }
}