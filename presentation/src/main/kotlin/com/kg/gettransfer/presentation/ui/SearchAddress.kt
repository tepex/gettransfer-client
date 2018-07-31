package com.kg.gettransfer.presentation.ui

import android.content.Context

import android.support.constraint.ConstraintLayout

import android.util.AttributeSet

import android.view.LayoutInflater

import android.widget.EditText

import com.kg.gettransfer.R

import timber.log.Timber

class SearchAddress @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0): ConstraintLayout(context, attrs, defStyleAttr) {
	
	private val address: EditText
	
	init {
		val root = LayoutInflater.from(context).inflate(R.layout.search_address, this, true)
		address = root.findViewById(R.id.address) as EditText
		if(attrs != null) {
			val ta = context.obtainStyledAttributes(attrs, R.styleable.SearchAddress)
			val hint = ta.getString(R.styleable.SearchAddress_hint)
			Timber.d("hint: $hint")
			address.setHint(hint)
			ta.recycle()
		}
	}
}
