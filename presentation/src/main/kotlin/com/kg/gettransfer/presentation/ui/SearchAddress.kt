package com.kg.gettransfer.presentation.ui

import android.content.Context

import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.support.constraint.ConstraintLayout

import android.text.Editable
import android.text.TextWatcher

import android.util.AttributeSet

import android.view.LayoutInflater
import android.view.View

import com.arellomobile.mvp.MvpDelegate
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.presentation.presenter.SearchAddressPresenter
import com.kg.gettransfer.presentation.view.SearchAddressView

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.search_address.*

import timber.log.Timber

/**
 *
 *
 * https://github.com/Arello-Mobile/Moxy/wiki/CustomView-as-MvpView
 *
 * LayoutContainer — Sic!
 * https://antonioleiva.com/kotlin-android-extensions/
 * https://antonioleiva.com/custom-views-android-kotlin/
 */
class SearchAddress @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer, SearchAddressView, TextWatcher {

    @InjectPresenter
    lateinit var presenter: SearchAddressPresenter
    private lateinit var parent: SearchActivity

    override val containerView: View
    /** From/To address flag */
    var isTo = false
        private set

    var text: String
        get() {
            return addressField.text.toString()
        }
        set(value) {
            addressField.setText(value)
        }
    private var parentDelegate: MvpDelegate<Any>? = null
    private val mvpDelegate by lazy {
        val ret = MvpDelegate<SearchAddress>(this)
        ret.setParentDelegate(parentDelegate!!, id.toString())
        ret
    }
    private var blockRequest = false
    private var hasFocus: Boolean = false

    init {
        containerView = LayoutInflater.from(context).inflate(R.layout.search_address, this, true)
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.SearchAddress)
            addressField.hint = ta.getString(R.styleable.SearchAddress_hint)
            ta.recycle()
        }

        clearBtn.setOnClickListener {
            text = ""
            presenter.onClearAddress(isTo)
            addressField.requestFocus()
        }
    }

    @ProvidePresenter
    fun createSearchAddressPresenter():
            SearchAddressPresenter = SearchAddressPresenter(parent.coroutineContexts,
            parent.router,
            parent.systemInteractor,
            parent.routeInteractor)

    fun initWidget(parent: SearchActivity, isTo: Boolean) {
        this.parent = parent
        this.isTo = isTo
        addressField.setOnFocusChangeListener { _, hasFocus ->
            this.hasFocus = hasFocus
            if (!hasFocus) {

                clearBtn.visibility = View.INVISIBLE
                presenter.returnAddress(isTo)

            } else {
                checkClearButtonVisibility()
                parent.presenter.isTo = isTo
                presenter.requestAddressListByPrediction(text.trim())
            }
        }
        addressField.addTextChangedListener(this)

        parentDelegate = parent.mvpDelegate
        mvpDelegate.onCreate()
        mvpDelegate.onAttach()

        presenter.mBounds = parent.mBounds

        if (isTo) addressField.requestFocus()
        checkClearButtonVisibility()
    }

    /** Set address text without request */
    fun initText(text: String, sendRequest: Boolean) {
        blockRequest = true
        this.text = text + " "
        if (addressField.hasFocus())
            addressField.setSelection(addressField.text.length)
        blockRequest = false
        if (sendRequest) presenter.requestAddressListByPrediction(text.trim())
    }

    fun setUneditable() {
        addressField.keyListener = null
        addressField.isCursorVisible = false
        addressField.isFocusable = false
        addressField.isClickable = true
    }

    fun clearFocusOnExit() {
        addressField.clearFocus()
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        addressField.setOnClickListener(l)
    }

    @CallSuper
    protected override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        /* Not used in MainActivity */
        if (parentDelegate == null) return

        mvpDelegate.onSaveInstanceState()
        mvpDelegate.onDetach()
    }

    override fun setAddressList(list: List<GTAddress>) {
        if (addressField.isFocused) parent.setAddressList(list)
    }

    override fun returnLastAddress(addressName: String) {
        text = addressName
    }

    override fun blockInterface(block: Boolean) {
        parent.blockInterface(block)
    }

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        if (addressField.isFocused) parent.setError(finish, errId, *args)
    }

    override fun setError(e: Throwable) {
        Timber.e(e)
        if (addressField.isFocused) parent.setError(e)
    }

    override fun afterTextChanged(s: Editable?) {
        checkClearButtonVisibility()
        if (!blockRequest) presenter.requestAddressListByPrediction(text.trim())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun setOnFocusChangeListener(listener: View.OnFocusChangeListener) {
        addressField.onFocusChangeListener = listener
    }

    private fun checkClearButtonVisibility() {
        if (text.isBlank()) {
            clearBtn.visibility = View.INVISIBLE
        } else {
            if (hasFocus) {
                clearBtn.visibility = View.VISIBLE
            }
        }
    }
}
