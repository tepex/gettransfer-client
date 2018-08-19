package com.kg.gettransfer.presentation.ui

import android.content.Context

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView

import android.text.Editable
import android.text.TextWatcher

import android.util.AttributeSet

import android.view.LayoutInflater
import android.view.View

import android.widget.EditText
import android.widget.ImageButton

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.GTAddress

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
class SearchAddress @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): 
	ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {

	override val containerView: View
	private lateinit var listView: RecyclerView
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
	
	fun initWidget(listView: RecyclerView, addressPrediction: String) {
		this.listView = listView
		if(!addressPrediction.isBlank()) {
			text = addressPrediction.trim()
			clearBtn.visibility = View.VISIBLE
		}
	}
	
	/**
	 * https://antonioleiva.com/lambdas-kotlin/
	 */
	inline fun onTextChanged(crossinline listener: (String) -> Unit) {
		addressField.addTextChangedListener(object: TextWatcher {
			override fun afterTextChanged(s: Editable?) { if(!implicitInput) listener(s?.toString()?.trim() ?: "") }
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
		})
	}
	
	inline fun onFocusChanged(crossinline listener: (sa: SearchAddress) -> Unit) {
		addressField.setOnFocusChangeListener { view, hasFocus -> if(hasFocus) listener(this@SearchAddress) }
	}
	
	inline fun onStartAddressSearch(crossinline listener: (SearchAddress) -> Unit) {
		addressField.setOnFocusChangeListener { _, hasFocus -> if(hasFocus) listener(this@SearchAddress) }
		addressField.addTextChangedListener(object: TextWatcher {
			override fun afterTextChanged(s: Editable?) { if(!implicitInput) listener(this@SearchAddress) }
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
		})
	}
}
