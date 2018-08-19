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

import android.widget.EditText
import android.widget.ImageButton

import com.arellomobile.mvp.MvpDelegate
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.interactor.AddressInteractor

import com.kg.gettransfer.presentation.presenter.SearchAddressPresenter
import com.kg.gettransfer.presentation.view.SearchAddressView
import com.kg.gettransfer.presentation.view.SearchView

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.search_address.*

import org.koin.android.ext.android.inject

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
class SearchAddress @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): 
	ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer, SearchAddressView, TextWatcher {

	@InjectPresenter
	lateinit var presenter: SearchAddressPresenter
	private lateinit var addressInteractor: AddressInteractor
	private lateinit var coroutineContexts: CoroutineContexts
	private lateinit var parent: SearchView

	override val containerView: View
	/** true — ввод с помощью setText(). Флаг предотвращает срабатывание onTextChanged */
	var implicitInput = false

	var text: String
		get() { return addressField.getText().toString() }
		set(value) {
			implicitInput = true
			addressField.setText(value)
			implicitInput = false
		}
	var address: GTAddress? = null
		set(value) {
			text = value?.address ?: ""
		}
	private lateinit var parentDelegate: MvpDelegate<Any>
	private val mvpDelegate by lazy {
		val ret = MvpDelegate<SearchAddress>(this)
		ret.setParentDelegate(parentDelegate, id.toString())
		ret
	}
	
	init {
		containerView = LayoutInflater.from(context).inflate(R.layout.search_address, this, true)
		if(attrs != null) {
			val ta = context.obtainStyledAttributes(attrs, R.styleable.SearchAddress)
			addressField.setHint(ta.getString(R.styleable.SearchAddress_hint))
			ta.recycle()
		}
		
		clearBtn.setOnClickListener { 
			address = null
			addressField.requestFocus()
		}
	}

	@ProvidePresenter
	fun createSearchAddressPresenter(): SearchAddressPresenter = SearchAddressPresenter(coroutineContexts,
		                                                                                addressInteractor)

	fun initWidget(parent: SearchActivity, addressPrediction: String) {
		this.parent = parent
		addressInteractor = parent.addressInteractor
		coroutineContexts = parent.coroutineContexts
		if(!addressPrediction.isBlank()) {
			text = addressPrediction.trim()
			clearBtn.visibility = View.VISIBLE
		}
		addressField.setOnFocusChangeListener(parent)
		addressField.addTextChangedListener(this)
		
		parentDelegate = parent.mvpDelegate
		mvpDelegate.onCreate()
		mvpDelegate.onAttach()
	}
	
	@CallSuper
	protected override fun onDetachedFromWindow() {
		super.onDetachedFromWindow()
		mvpDelegate.onSaveInstanceState()
		mvpDelegate.onDetach()
	}
	
	fun requestAddresses() {
		presenter.requestAddressListByPrediction(text.trim())
	}
	
	override fun setAddressList(list: List<GTAddress>) = parent.setAddressList(list)
	override fun setError(@StringRes errId: Int, finish: Boolean) = parent.setError(errId, finish)
	
	/**
	 * https://antonioleiva.com/lambdas-kotlin/
	 */
	override fun afterTextChanged(s: Editable?) { if(!implicitInput) requestAddresses() }
	override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
	override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
	
	inline fun onStartAddressSearch(crossinline listener: (SearchAddress) -> Unit) {
		addressField.setOnFocusChangeListener { _, hasFocus -> if(hasFocus) listener(this@SearchAddress) }
		addressField.addTextChangedListener(object: TextWatcher {
			override fun afterTextChanged(s: Editable?) { if(!implicitInput) listener(this@SearchAddress) }
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
		})
	}
}
