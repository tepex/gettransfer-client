package com.kg.gettransfer.presentation.ui

import android.content.Context

import android.support.constraint.ConstraintLayout

import android.util.AttributeSet

import android.widget.ImageView
import android.widget.EditText

import com.kg.gettransfer.R

import kotlinx.android.synthetic.main.search_address.*

import timber.log.Timber

class SearchAddress: ConstraintLayout {
	constructor(context: Context): super(context) {
		initLayout()
	}
	
	constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
		initLayout()
	}
	
	constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
		initLayout()
	}
	
	private fun initLayout() {
		inflate(context, R.layout.search_address, this)
    }
}
