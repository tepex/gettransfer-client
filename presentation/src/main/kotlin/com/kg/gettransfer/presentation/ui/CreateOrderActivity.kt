package com.kg.gettransfer.presentation.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.interactor.AddressInteractor

import com.kg.gettransfer.presentation.presenter.CreateOrderPresenter
import com.kg.gettransfer.presentation.view.CreateOrderView
import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.presenter.LicenceAgreementPresenter
import com.kg.gettransfer.presentation.presenter.SearchPresenter

import android.support.v7.app.AlertDialog
import android.text.InputType
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.PopupWindow

import com.arellomobile.mvp.presenter.ProvidePresenter

import kotlinx.android.synthetic.main.activity_transfer.*
import kotlinx.android.synthetic.main.layout_popup_comment.*
import kotlinx.android.synthetic.main.layout_popup_comment.view.*

import org.koin.android.ext.android.inject

import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportAppNavigator

import java.util.Calendar

class CreateOrderActivity : MvpAppCompatActivity(), CreateOrderView {

    @InjectPresenter
    internal lateinit var presenter: CreateOrderPresenter

    private val navigatorHolder: NavigatorHolder by inject()
    private val router: Router by inject()
	val addressInteractor: AddressInteractor by inject()
	val coroutineContexts: CoroutineContexts by inject()

    var mYear = 0
    var mMonth = 0
    var mDay = 0
    var mHour = 0
    var mMinute = 0

    @ProvidePresenter
    fun createCreateOrderPresenter(): CreateOrderPresenter = CreateOrderPresenter(coroutineContexts,
                                                                                  router,
                                                                                  addressInteractor)

    private val navigator: Navigator = object: SupportAppNavigator(this, Screens.NOT_USED) {
        protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
            when(screenKey) {
                Screens.LICENCE_AGREE -> return Intent(this@CreateOrderActivity, LicenceAgreementActivity::class.java)
            }
            return null
        }
        protected override fun createFragment(screenKey: String, data: Any?): Fragment? = null
    }

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

    @CallSuper
    protected override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    private fun setOnClickListeners() {
        tvPersonsCounterDown.setOnClickListener(clickListenerCounterButtons)
        tvPersonsCounterUp.setOnClickListener(clickListenerCounterButtons)
        tvChildCounterDown.setOnClickListener(clickListenerCounterButtons)
        tvChildCounterUp.setOnClickListener(clickListenerCounterButtons)

        btnChangeCurrencyType.setOnClickListener { view -> showDialogChangeCurrency() }
        layoutDateTimeTransfer.setOnClickListener { view -> changeDateTime(true) }
        tvComments.setOnClickListener { view -> showPopupWindowComment() }
        layoutAgreement.setOnClickListener { view -> presenter.showLicenceAgreement() }
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
    
    override fun setRoute(route: Pair<GTAddress, GTAddress>) {
    	tvFrom.setText(route.first.name)
    	tvTo.setText(route.second.name)
    }
}