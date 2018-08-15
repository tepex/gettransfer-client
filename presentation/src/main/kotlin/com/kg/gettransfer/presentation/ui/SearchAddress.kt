package com.kg.gettransfer.presentation.ui

import android.content.Context

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView

import android.util.AttributeSet

import android.view.LayoutInflater

import android.widget.EditText
import android.widget.ImageButton

import com.kg.gettransfer.R

import timber.log.Timber

/**
 * https://github.com/Arello-Mobile/Moxy/wiki/CustomView-as-MvpView
 */
class SearchAddress @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): 
	ConstraintLayout(context, attrs, defStyleAttr) {
	
	val address: EditText
	private val clearBtn: ImageButton
	private lateinit var listView: RecyclerView
	
	init {
		val root = LayoutInflater.from(context).inflate(R.layout.search_address, this, true)
		address = root.findViewById(R.id.address) as EditText
		if(attrs != null) {
			val ta = context.obtainStyledAttributes(attrs, R.styleable.SearchAddress)
			address.setHint(ta.getString(R.styleable.SearchAddress_hint))
			ta.recycle()
		}
		clearBtn = root.findViewById(R.id.clearBtn) as ImageButton
		clearBtn.setOnClickListener {
			address.setText("")
		}
	}
	
	fun initWidget(listView: RecyclerView, addressPrediction: String?) {
		this.listView = listView
		address.setText(addressPrediction)
	}
}
