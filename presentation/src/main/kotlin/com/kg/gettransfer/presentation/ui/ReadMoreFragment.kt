package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R

import kotlinx.android.synthetic.main.dialog_read_more.btnClose

class ReadMoreFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_read_more, container, false)
        dialog.setTitle(R.string.LNG_BESTPRICE_TITLE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnClose.setOnClickListener { dialog.dismiss() }
    }
}
