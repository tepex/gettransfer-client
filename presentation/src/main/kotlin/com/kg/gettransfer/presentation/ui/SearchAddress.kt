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

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.search_address.*

import timber.log.Timber

/**
 * https://github.com/Arello-Mobile/Moxy/wiki/CustomView-as-MvpView
 * 
 * LayoutContainer — важно!!!
 * https://antonioleiva.com/kotlin-android-extensions/
 * https://antonioleiva.com/custom-views-android-kotlin/
 */
class SearchAddress @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): 
	ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {

	override val containerView: View
	private lateinit var listView: RecyclerView
	/** Ввод с помощью setText(). Флаг предотвращает срабатывание afterTextChanged */
	var implicitInput = false
	
	var text: String
		get() { return address.getText().toString() }
		set(value) {
			implicitInput = true
			address.setText(value)
			implicitInput = false
		}
	
	init {
		containerView = LayoutInflater.from(context).inflate(R.layout.search_address, this, true)
		if(attrs != null) {
			val ta = context.obtainStyledAttributes(attrs, R.styleable.SearchAddress)
			address.setHint(ta.getString(R.styleable.SearchAddress_hint))
			ta.recycle()
		}
		clearBtn.setOnClickListener { text = "" }
	}
	
	fun initWidget(listView: RecyclerView, addressPrediction: String?) {
		this.listView = listView
		text = addressPrediction ?: ""
	}
	
	/**
	 * https://antonioleiva.com/lambdas-kotlin/
	 */
	inline fun onTextChanged(crossinline listener: (String) -> Unit) {
		address.addTextChangedListener(object: TextWatcher {
			override fun afterTextChanged(s: Editable?) { if(!implicitInput) listener(s?.toString()?.trim() ?: "") }
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
		})
	}
}
