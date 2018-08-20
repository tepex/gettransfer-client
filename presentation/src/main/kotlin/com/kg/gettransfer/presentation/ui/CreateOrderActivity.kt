package com.kg.gettransfer.presentation.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.CreateOrderPresenter
import com.kg.gettransfer.presentation.view.CreateOrderView
import kotlinx.android.synthetic.main.activity_transfer.*
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import kotlinx.android.synthetic.main.layout_popup_comment.*
import kotlinx.android.synthetic.main.layout_popup_comment.view.*
import java.util.*


class CreateOrderActivity : MvpAppCompatActivity(), CreateOrderView {

    @InjectPresenter
    internal lateinit var presenter: CreateOrderPresenter

    var mYear = 0
    var mMonth = 0
    var mDay = 0
    var mHour = 0
    var mMinute = 0

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_transfer)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }

        changeDateTime(false)

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        tvPersonsCounterDown.setOnClickListener(clickListenerCounterButtons)
        tvPersonsCounterUp.setOnClickListener(clickListenerCounterButtons)
        tvChildCounterDown.setOnClickListener(clickListenerCounterButtons)
        tvChildCounterUp.setOnClickListener(clickListenerCounterButtons)
        btnChangeCurrencyType.setOnClickListener { showDialogChangeCurrency() }
        layoutDateTimeTransfer.setOnClickListener { changeDateTime(true) }
        tvComments.setOnClickListener { showPopupWindowComment() }
    }

    private val clickListenerCounterButtons = View.OnClickListener { view ->
        when (view.id) {
            R.id.tvPersonsCounterDown -> presenter.changeCounter(tvCountPerson, -1)
            R.id.tvPersonsCounterUp -> presenter.changeCounter(tvCountPerson, 1)
            R.id.tvChildCounterDown -> presenter.changeCounter(tvCountChild, -1)
            R.id.tvChildCounterUp -> presenter.changeCounter(tvCountChild, 1)
        }
    }

    private fun showPopupWindowComment(){
        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        val screenHeight = displaymetrics.heightPixels

        val layoutPopup = LayoutInflater.from(applicationContext).inflate(R.layout.layout_popup_comment, layoutPopup)
        val popupWindowComment = PopupWindow(layoutPopup, LinearLayout.LayoutParams.MATCH_PARENT, screenHeight / 3, true)
        layoutPopup.etPopupComment.setText(tvComments.text)
        layoutPopup.etPopupComment.setRawInputType(InputType.TYPE_CLASS_TEXT)
        popupWindowComment.showAtLocation(mainLayoutActivityTransfer, Gravity.CENTER, 0, 0)
        layoutShadow.visibility = View.VISIBLE

        layoutPopup.btnClearPopupComment.setOnClickListener { layoutPopup.etPopupComment.setText("") }
        layoutPopup.etPopupComment.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                presenter.setComment(layoutPopup.etPopupComment.text.toString())
                popupWindowComment.dismiss()
                return@OnEditorActionListener true
            }
            false
        })
        popupWindowComment.setOnDismissListener {
            hideKeyboard()
            layoutShadow.visibility = View.GONE
        }
        layoutPopup.setOnClickListener{ layoutPopup.etPopupComment.requestFocus()}
        layoutPopup.etPopupComment.setSelection(layoutPopup.etPopupComment.text.length)
        showKeyboard()
    }

    private fun showDialogChangeCurrency() {
        val currencies = arrayOf("US Dollar ($)", "Euro (€)", "Pound Sterling (£)",
                "Russian Ruble (\u20BD)", "Baht (฿)", "Renminbi(¥)")
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.currency)
        builder.setItems(currencies) { _, which -> presenter.changeCurrency(which) }
        builder.setNegativeButton(R.string.cancel, null)
        builder.show()
    }

    private fun changeDateTime(showDialog: Boolean){
        val calendar = Calendar.getInstance()
        mYear = calendar.get(Calendar.YEAR)
        mMonth = calendar.get(Calendar.MONTH)
        mDay = calendar.get(Calendar.DAY_OF_MONTH)
        mHour = calendar.get(Calendar.HOUR_OF_DAY)
        mMinute = calendar.get(Calendar.MINUTE)
        if (showDialog) showDatePickerDialog() else presenter.changeDateTimeTransfer(mYear, mMonth, mDay, mHour, mMinute)
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            run {
                mYear = year
                mMonth = monthOfYear
                mDay = dayOfMonth
                showTimePickerDialog()
            }
        }, mYear, mMonth, mDay)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            run {
                mHour = hour
                mMinute = minute
                presenter.changeDateTimeTransfer(mYear, mMonth, mDay, mHour, mMinute)
            }
        }, mHour, mMinute, true)
        timePickerDialog.show()
    }

    private fun showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if(view != null)
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onBackPressed() {
        presenter.onBackCommandClick()
    }

    override fun setCounters(textViewCounter: TextView, count: Int) {
        textViewCounter.text = count.toString()
    }

    override fun setCurrency(currencySymbol: CharSequence) {
        tvCurrencyType.text = currencySymbol
    }

    override fun setDateTimeTransfer(dateTimeString: String) {
        tvDateTimeTransfer.text = dateTimeString
        tvOrderDateTime.text = dateTimeString
    }

    override fun setComment(comment: String) {
        tvComments.text = comment
    }
}