package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import com.kg.gettransfer.R


class TextFieldActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_text_field)
		window.setGravity(Gravity.BOTTOM)
	}

}