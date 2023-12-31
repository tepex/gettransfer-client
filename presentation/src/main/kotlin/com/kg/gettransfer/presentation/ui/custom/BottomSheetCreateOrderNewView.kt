package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView

import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.hideKeyboard
import androidx.core.view.isVisible
import com.kg.gettransfer.extensions.setThrottledClickListener
import com.kg.gettransfer.extensions.showKeyboard

import com.kg.gettransfer.presentation.adapter.TransferTypeAdapter
import com.kg.gettransfer.presentation.delegate.DateTimeDelegate
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.presenter.CreateOrderPresenter
import com.kg.gettransfer.presentation.ui.CreateOrderActivity
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.presentation.ui.onTextChanged
import com.kg.gettransfer.presentation.view.CreateOrderView

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.bottom_sheet_create_order.*
import kotlinx.android.synthetic.main.layout_hourly_duration.view.*
import kotlinx.android.synthetic.main.view_count_controller.view.*
import kotlinx.android.synthetic.main.view_create_order_field.*
import kotlinx.android.synthetic.main.view_selected_currency.tv_currency

@Suppress("TooManyFunctions")
class BottomSheetCreateOrderNewView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attributeSet, defStyle), LayoutContainer {

    override val containerView: View =
        LayoutInflater.from(context).inflate(R.layout.bottom_sheet_create_order, this, true)

    private lateinit var adapter: TransferTypeAdapter

    var withReturnWay: Boolean
        get() = rl_returnWayTime.isVisible
        set(value) {
            rl_returnWayTime.isVisible = value
        }

    var price: Double?
        get() = price_field_input.text.toDoubleOrNull()
        set(value) {
            value?.let { price_field_input.text = value.toString() }
        }

    var flightNumber: String?
        get() = flight_number_field.text.trim()
        set(value) {
            value?.let { flight_number_field.text = value }
        }

    var flightNumberReturn: String?
        get() = flight_numberReturn_field.text.trim()
        set(value) {
            value?.let { flight_numberReturn_field.text = value }
        }

    var promoCode: String
        get() = promo_field.text
        set(value) {
            if (value.isNotEmpty()) {
                promo_field.text = value
            }
        }

    var currency: String
        get() = tv_currency.text.toString().trim()
        set(value) {
            tv_currency.text = value
        }

    var comment: String
        get() = comment_field.text.trim()
        set(value) {
            comment_field.text = value
        }

    var showAgreement: Boolean
        get() = layoutAgreement.isVisible
        set(value) {
            layoutAgreement.isVisible = value
        }

    var isAcceptedAgreement: Boolean
        get() = switchAgreement.isChecked
        set(value) {
            switchAgreement.isChecked = value
        }

    var listener: OnCreateOrderListener? = null

    private var hasErrorFields = false
    private var errorFieldView: View? = null

    private val onTouchListener = View.OnTouchListener { view, event ->
        if (event.action == MotionEvent.ACTION_MOVE) {
            view.hideKeyboard()
            return@OnTouchListener false
        } else {
            return@OnTouchListener false
        }
    }

    init {
        scrollContent.setOnTouchListener(onTouchListener)

        initFieldsViews()

        price_field_input.field_input.onTextChanged            { listener?.onPriceChanged(it.toDoubleOrNull()) }
        price_field_input.field_input.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) { listener?.onPriceFocused() }
        }
        select_currency.setOnClickListener                     { listener?.onCurrencyClick() }
        sign_name_field.field_input.onTextChanged              { listener?.onNameChanged(it.trim()) }
        flight_number_field.field_input.onTextChanged          { listener?.onFlightNumberChanged(it.trim()) }
        flight_numberReturn_field.field_input.onTextChanged    { listener?.onFlightNumberReturnChanged(it.trim()) }
        promo_field.field_input.onTextChanged                  { listener?.onPromoCodeChanged(it) }

        hourly_duration.setOnClickListener                      { listener?.onHourlyDurationClick() }
        transfer_date_time_field.setOnClickListener             { listener?.onTransferDateTimeClick() }
        transfer_date_time_field.field_input.setOnClickListener { listener?.onTransferDateTimeClick() }

        fl_DeleteReturnDate.setOnClickListener {
            listener?.onDeleteReturnDateTimeClick()
            showReturnFlight(CreateOrderActivity.HIDE)
        }
        transfer_return_date_field.setOnClickListener { listener?.onTransferReturnDateTimeClick() }
        transfer_return_date_field.field_input.setOnClickListener { listener?.onTransferReturnDateTimeClick() }

        passengers_count.img_plus_seat.setOnClickListener {
            checkErrorField(passengers_count_field)
            listener?.onPassengersCountInc()
        }
        passengers_count.img_minus_seat.setOnClickListener { listener?.onPassengersCountDec() }
        children_seat_field.setOnClickListener             { listener?.onChildrenSeatClick() }
        children_seat_field.field_input.setOnClickListener { listener?.onChildrenSeatClick() }
        comment_field.field_input.setOnClickListener       { listener?.onCommentClick(comment) }
        field_input.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) listener?.checkPromoCode() }

        offerPrice.setOnClickListener      { fieldTouched(price_field_input.field_input)  }
        sign_name_field.setOnClickListener     { fieldTouched(sign_name_field.field_input) }
        flight_number_field.setOnClickListener { fieldTouched(flight_number_field.field_input) }

        tvAgreement1.setOnClickListener { listener?.onAgreementClick() }
        switchAgreement.setOnCheckedChangeListener { _, isChecked ->
            checkErrorField(layoutAgreement)
            listener?.onAgreementChecked(isChecked)
        }

        btnGetOffers.setThrottledClickListener {
            clearHighLightErrorField(errorFieldView)
            listener?.onGetOffersClick()
        }
    }

    private fun initFieldsViews() {
        passengers_count.person_count.text = context.getString(R.string.passenger_number_default)
        sign_name_field.field_input.filters =
            arrayOf<InputFilter>(InputFilter.LengthFilter(CreateOrderActivity.SIGN_NAME_FIELD_MAX_LENGTH))
        field_input.filters = arrayOf(InputFilter.AllCaps())

        rvTransferType.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvTransferType.isNestedScrollingEnabled = false
        adapter = TransferTypeAdapter { transportType, showInfo ->
            checkErrorField(rvTransferType)
            listener?.onTransportTypeClicked(transportType, showInfo)
        }
        rvTransferType.adapter = adapter
    }

    fun setPromoResult(discountInfo: String?) {
        @ColorRes
        var colorText = R.color.color_error
        var text = context.getString(R.string.LNG_RIDE_PROMOCODE_INVALID)

        discountInfo?.let { di ->
            colorText = R.color.colorGreen
            text = di
        }
        promo_field.field_input.setTextColor(ContextCompat.getColor(context, colorText))
        promo_field.hint = text
    }

    fun disablePromoView() {
        promo_field.field_input.isEnabled = false
    }

    fun resetPromoView() {
        promo_field.hint = context.getString(R.string.LNG_RIDE_PROMOCODE_PLACEHOLDER)
        promo_field.field_input.setTextColor(ContextCompat.getColor(context, R.color.colorTextLightGray))
    }

    private fun fieldTouched(viewForFocus: EditText) {
// TODO()        if (!isKeyBoardOpened) {
            showKeyboard()
//        }
        with(viewForFocus) {
            requestFocus()
            post { setSelection(text.length) }
        }
    }

    fun setHourlyDuration(durationValue: Int?) {
        if (durationValue != null && durationValue > 0) {
            hourly_duration.isVisible = true
            hourly_duration.tvCurrent_hours.text = HourlyValuesHelper.getValue(durationValue, context)
        }
    }

    fun setTransferDateTime(date: String, field: Boolean) {
        if (field == DateTimeDelegate.START_DATE) {
            transfer_date_time_field.text = date
        } else {
            showReturnFlight(CreateOrderActivity.SHOW)
            transfer_return_date_field.text = date
        }
        checkErrorField(transfer_date_time_field)
    }

    val showReturnFlight: (show: Boolean) -> Unit = { show ->
        flight_numberReturn_field.isVisible   = show
        flight_numberReturn_divider.isVisible = show
        fl_DeleteReturnDate.isVisible         = show
        if (!show) {
            transfer_return_date_field.field_input.text?.clear()
            flight_numberReturn_field.field_input.text?.clear()
            transfer_return_date_field.hint = context.getString(R.string.LNG_RIDE_DATE_RETURN)
        } else {
            transfer_return_date_field.hint = context.getString(R.string.LNG_RIDE_RETURN_TRANSFER)
        }
        changeReturnTransferIcon(show)
    }

    private fun changeReturnTransferIcon(show: Boolean) {
        if (show) {
            Utils.setDrawables(transfer_return_date_field.field_input, R.drawable.ic_calendar, 0, 0, 0)
        } else {
            Utils.setDrawables(
                transfer_return_date_field.field_input,
                R.drawable.ic_plus,
                0,
                R.drawable.ic_arrow_right,
                0
            )
        }
    }

    private fun checkMinusButton(count: Int, view: ImageView) {
        val imgRes =
            if (count == CreateOrderPresenter.MIN_PASSENGERS) R.drawable.ic_minus_disabled else R.drawable.ic_minus
        view.setImageDrawable(ContextCompat.getDrawable(context, imgRes))
    }

    fun setPassengers(count: Int) {
        passengers_count.person_count.text = "$count"
        checkMinusButton(count, passengers_count.img_minus_seat)
    }

    fun setChildSeats(setOf: Set<CreateOrderView.ChildSeatItem>, total: Int) {
        if (total == 0) {
            children_seat_field.text = ""
            children_seat_field.hint = context.getString(R.string.LNG_NO_SEATS_REQUIRED)
            return
        }

        val text = buildString {
            if (total > 1) {
                append("$total ")
            }
            append(setOf.joinToString(prefix = "(", postfix = ")") { item ->
                buildString {
                    if (item.count > 1) {
                        append("${item.count}x ")
                    }
                    append(context.getString(item.stringId))
                }
            })
        }
        children_seat_field.text = text
        children_seat_field.hint = context.getString(R.string.LNG_RIDE_CHILDREN)
    }

    fun updateTypes(types: List<TransportTypeModel>) {
        adapter.update(types)
        adapter.notifyDataSetChanged()
    }

    fun highLightErrorField(errorField: CreateOrderView.FieldError) {
        hasErrorFields = true
        when (errorField) {
            CreateOrderView.FieldError.TRANSPORT_FIELD -> {
                highLightErrorField(rvTransferType)
                errorFieldView = rvTransferType
            }
            CreateOrderView.FieldError.TIME_NOT_SELECTED -> {
                highLightErrorField(transfer_date_time_field)
                errorFieldView = transfer_date_time_field
            }
            CreateOrderView.FieldError.RETURN_TIME -> {
                highLightErrorField(transfer_return_date_field)
                errorFieldView = transfer_return_date_field
            }
            CreateOrderView.FieldError.PASSENGERS_COUNT -> highLightErrorField(passengers_count_field)
            CreateOrderView.FieldError.TERMS_ACCEPTED_FIELD -> highLightErrorField(layoutAgreement)
            else -> return
        }
    }

    private fun highLightErrorField(view: View) {
        view.setBackgroundResource(R.drawable.background_field_error)
        scrollContent.smoothScrollTo(0, view.top)
    }

    private fun checkErrorField(view: View) {
        if (hasErrorFields) {
            hasErrorFields = false
            clearHighLightErrorField(view)
        }
    }

    private fun clearHighLightErrorField(view: View?) = view?.setBackgroundResource(0)

    @Suppress("TooManyFunctions")
    interface OnCreateOrderListener {
        fun checkPromoCode()
        fun onCurrencyClick()
        fun onPriceChanged(value: Double?)
        fun onPriceFocused()
        fun onNameChanged(value: String)
        fun onFlightNumberChanged(value: String)
        fun onFlightNumberReturnChanged(value: String)
        fun onPromoCodeChanged(value: String)

        fun onHourlyDurationClick()
        fun onTransferDateTimeClick()
        fun onTransferReturnDateTimeClick()
        fun onDeleteReturnDateTimeClick()
        fun onPassengersCountInc()
        fun onPassengersCountDec()
        fun onChildrenSeatClick()
        fun onCommentClick(value: String)
        fun onAgreementClick()
        fun onAgreementChecked(value: Boolean)
        fun onTransportTypeClicked(type: TransportTypeModel, showInfo: Boolean)

        fun onGetOffersClick()
    }
}
