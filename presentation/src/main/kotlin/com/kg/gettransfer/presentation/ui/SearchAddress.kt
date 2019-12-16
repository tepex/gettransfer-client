package com.kg.gettransfer.presentation.ui

import android.content.Context

import androidx.annotation.CallSuper
import androidx.annotation.StringRes

import androidx.constraintlayout.widget.ConstraintLayout

import android.text.Editable
import android.text.TextWatcher

import android.util.AttributeSet

import android.view.LayoutInflater
import android.view.View
import androidx.core.content.withStyledAttributes

import moxy.MvpDelegate
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.extensions.isInvisible

import com.kg.gettransfer.presentation.presenter.SearchAddressPresenter
import com.kg.gettransfer.presentation.view.SearchAddressView

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.search_address.*

/**
 * https://github.com/Arello-Mobile/Moxy/wiki/CustomView-as-MvpView
 *
 * LayoutContainer — Sic!
 * https://antonioleiva.com/kotlin-android-extensions/
 * https://antonioleiva.com/custom-views-android-kotlin/
 */

@Suppress("TooManyFunctions")
class SearchAddress @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer, SearchAddressView, TextWatcher {

    @InjectPresenter
    lateinit var presenter: SearchAddressPresenter
    private lateinit var parent: SearchFragment

    override val containerView: View
    /** From/To address flag */
    var isTo = false
        private set

    var text: String
        get() = addressField.text.toString()
        set(value) { addressField.setText(value) }

    private var parentDelegate: MvpDelegate<Any>? = null
    private val mvpDelegate by lazy {
        MvpDelegate<SearchAddress>(this).apply { parentDelegate?.let { setParentDelegate(it, id.toString()) } }
    }
    private var blockRequest = false
    private var hasFocus     = false

    init {
        containerView = LayoutInflater.from(context).inflate(R.layout.search_address, this, true)
        context.withStyledAttributes(attrs, R.styleable.SearchAddress) {
            addressField.hint = getString(R.styleable.SearchAddress_hint)
        }

        val clearListener = OnClickListener {
            text = ""
            addressField.requestFocus()
            parent.onSearchFieldEmpty()
        }
        im_clearBtn.setOnClickListener(clearListener)
    }

    @ProvidePresenter
    fun createSearchAddressPresenter() = SearchAddressPresenter()

    fun initWidget(parent: SearchFragment, isTo: Boolean) {
        this.parent = parent
        this.isTo = isTo
        addressField.setOnFocusChangeListener { _, hasFocus ->
            this.hasFocus = hasFocus
            if (!hasFocus) {
                im_clearBtn.isVisible = false
            } else {
                setClearButtonVisibility()
                parent.presenter.isTo = isTo
                if (text.trim().length >= SearchAddressPresenter.ADDRESS_PREDICTION_SIZE) {
                    presenter.requestAddressListByPrediction(text.trim())
                } else {
                    parent.onSearchFieldEmpty()
                }
            }
        }
        addressField.addTextChangedListener(this)

        parentDelegate = parent.mvpDelegate
        mvpDelegate.onCreate()
        mvpDelegate.onAttach()
        setClearButtonVisibility()
    }

    /** Set address text without request */
    fun initText(text: String, sendRequest: Boolean, cursorOnEnd: Boolean) {
        blockRequest = true
        this.text = if (text.isNotEmpty()) "$text " else ""
        if (cursorOnEnd) addressField.setSelection(addressField.text?.length ?: 0)
        blockRequest = false
        if (sendRequest) presenter.requestAddressListByPrediction(text.trim())
    }

    fun setUneditable() {
        with(addressField) {
            keyListener     = null
            isCursorVisible = false
            isFocusable     = false
            isClickable     = true
        }
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
        if (addressField.isFocused) parent.setAddressListByAutoComplete(list)
    }

    override fun returnLastAddress(addressName: String) {
        text = addressName
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        parent.blockInterface(block, useSpinner)
    }

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        if (addressField.isFocused) parent.setError(finish, errId, *args)
    }

    override fun setError(e: ApiException) {
        if (addressField.isFocused) parent.setError(e)
    }

    override fun setError(e: DatabaseException) {
        if (addressField.isFocused) parent.setError(e)
    }

    override fun afterTextChanged(s: Editable?) {
        setClearButtonVisibility()
        if (!blockRequest && text.trim().length >= SearchAddressPresenter.ADDRESS_PREDICTION_SIZE) {
            presenter.requestAddressListByPrediction(text.trim())
        } else {
            parent.onSearchFieldEmpty()
        }
        if (s.isNullOrBlank() && addressField.hasFocus()) {
            parent.onSearchFieldEmpty()
            presenter.onClearAddress(isTo)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun setOnFocusChangeListener(listener: View.OnFocusChangeListener) {
        addressField.onFocusChangeListener = listener
    }

    private fun setClearButtonVisibility() {
        if (text.isBlank()) {
            im_clearBtn.isInvisible = true
        } else if (hasFocus) {
            im_clearBtn.isInvisible = false
        }
    }

    fun changeFocus() {
        addressField.requestFocus()
        addressField.setSelection(addressField.text?.length ?: 0)
    }

    override fun setTransferNotFoundError(transferId: Long, dismissCallBack: (() -> Unit)?) {}
}
