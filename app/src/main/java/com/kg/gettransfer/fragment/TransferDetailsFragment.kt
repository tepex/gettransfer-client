package com.kg.gettransfer.fragment


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
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
import com.kg.gettransfer.modules.ProfileModel
import com.kg.gettransfer.modules.PromoCodeModel
import com.kg.gettransfer.modules.TransfersModel
import com.kg.gettransfer.modules.http.json.NewTransfer
import com.kg.gettransfer.modules.http.json.PassengerProfile
import com.kg.gettransfer.modules.http.json.Trip
import com.kg.gettransfer.realm.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
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
    private val profileModel: ProfileModel by inject()

    private var savedView: View? = null

    private val pax: Int? get() = etPassengers.text.toString().toIntOrNull()

    private val disposables = CompositeDisposable()

    private lateinit var transfer: NewTransfer

    fun setTransfer(t: NewTransfer) {
        transfer = t
        updateUIFromTransfer()
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
            savedView = v

            v.btnPassengersDec.setOnClickListener { passengersDec() }
            v.btnPassengersInc.setOnClickListener { passengersInc() }

            v.fabCreate.setOnClickListener { createTransfer(transfer) }

            v.btnHavePromoCode.setOnClickListener {
                btnHavePromoCode.visibility = GONE
                tvlHavePromoCode.maxHeight = (resources.displayMetrics.density * 16).toInt()
                clPromoCode.visibility = VISIBLE
                etPromoCode.requestFocus()
            }

            if (!::transfer.isInitialized) {
                fragmentManager?.popBackStackImmediate()
            } else {
                v.tfTransport.updatePrices(transfer)

                installEditTextWatcher(v)

                initPromoCodeUI(v.etPromoCode, v.tvPromoInfo, v.tvPromoValidation, v.pbPromoValidation)

                v.btnBack.setOnClickListener { fragmentManager?.popBackStackImmediate() }

                updateUIFromTransfer()
            }
        }

        return v
    }


    private fun updateUIFromTransfer() {
        val v = savedView ?: return
        v.tvFromDetails.text = transfer.from?.name
        v.ivToDetails.setImageResource(
                if (transfer.hireDuration ?: 0 <= 0) R.drawable.ic_arrow_blue_16dp
                else R.drawable.ic_timer_blue_16dp)

        val activity = activity ?: return
        v.tvToDetails.text =
                transfer.to?.name ?: Utils.hoursToString(activity, transfer.hireDuration ?: 0)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etSign.setText(profileModel.profile.fullName)

        if (!profileModel.profile.valid) {
            var d: Disposable? = null
            d = profileModel.addOnProfileUpdated {
                etSign.setText(it.fullName)
                d?.dispose()
            }
        }

        etEmail.setText(currentAccount.accountInfo.email)
        etPhone.setText(currentAccount.accountInfo.phone)

        view.post {
            svTransfer.fullScroll(FOCUS_UP)
            svTransfer.scrollTo(0, 0)
        }

        if (savedView != null) savedView?.tfTransport?.updatePrices(transfer)
    }


    override fun onResume() {
        super.onResume()
        updateUIFromTransfer()
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

                                    fragmentManager?.popBackStackImmediate()

                                    act.showTransfers(null)
                                },
                                {
                                    setBusy(false)
                                    tvError.text = getString(R.string.error_creating_request) +
                                            "\n" + it.message
                                    tvError.setTextColor(resources.getColor(R.color.colorTextError))
                                    fabCreate.visibility = VISIBLE
                                    svTransfer.post {
                                        svTransfer.fullScroll(FOCUS_DOWN)
                                    }
                                }))
    }


    private fun setBusy(b: Boolean) {
        if (b) {
            fabCreate.visibility = INVISIBLE
            progressBar.visibility = VISIBLE
            fabProgress.visibility = VISIBLE
            tvError.text = activity?.getString(R.string.dont_worry)
            tvError.setTextColor(resources.getColor(R.color.colorTextGray))
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
        btnPassengersDec.visibility = if (pax ?: 0 > 0) VISIBLE else INVISIBLE
        fabCreate.visibility = if (fieldsValidate()) VISIBLE else GONE
    }


    private fun fieldsValidate(): Boolean {
        var message = ""

        if (etDate.text.isEmpty()) {
            message += ", " + getString(R.string.date)
        }
        if (etTime.text.isEmpty()) {
            message += ", " + getString(R.string.time)
        }
        if ((pax ?: 0) < 1) {
            message += ", " + getString(R.string.passengers)
        }

        if (tfTransport.typesIDs.isEmpty()) {
            message += ", " + getString(R.string.transport)
        }

        if (etSign.text.isEmpty()) {
            message += ", " + getString(R.string.name_sign)
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.text).matches()) {
            message += ", " + getString(R.string.email)
        }
        if (!android.util.Patterns.PHONE.matcher(etPhone.text).matches()) {
            message += ", " + getString(R.string.phone)
        }

        if (message.isNotEmpty()) {
            message = String.format(
                    getString(R.string.please_fill_to_get_offers),
                    message.substring(2).toLowerCase())
            tvError.text = message
            tvError.setTextColor(resources.getColor(R.color.colorTextError))
            return false
        } else {
            tvError.text = activity?.getString(R.string.dont_worry)
            tvError.setTextColor(resources.getColor(R.color.colorTextGray))
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
            if (it.isNotEmpty()) {
                tvPromoInfo.visibility = VISIBLE
                tvPromoInfo.text = it
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
        t.currency = cfCurrency.text.toString()
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

        etPromoCode.editableText.clear()

        clPromoCode.visibility = GONE
        tvPromoValidation.visibility = GONE
        btnHavePromoCode.visibility = VISIBLE
    }
}